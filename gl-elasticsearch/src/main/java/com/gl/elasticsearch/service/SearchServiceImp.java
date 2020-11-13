package com.gl.elasticsearch.service;

import ES.EsProductComb;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gl.elasticsearch.config.ElasticSearchConfig;
import com.gl.elasticsearch.consts.EsConst;
import com.gl.elasticsearch.feign.ProductFeign;
import com.gl.elasticsearch.vo.AttributeVo;
import com.gl.elasticsearch.vo.ProductBrandVo;
import com.gl.elasticsearch.vo.SearchParams;
import com.gl.elasticsearch.vo.SearchResult;
import com.glmall.utils.R;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.tomcat.util.security.Escape;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchServiceImp implements SearchService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ProductFeign productFeign;

    @Override
    public SearchResult search(SearchParams searchParams) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isBlank(searchParams.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("productCombTitle", searchParams.getKeyword()));
        }
        if (!StringUtils.isBlank(searchParams.getCatalog3Id())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId", searchParams.getCatalog3Id()));
        }
        if (searchParams.getBrandId() != null && searchParams.getBrandId().size() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParams.getBrandId()));
        }
        if (searchParams.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchParams.getHasStock() == 1));
        }
        if (!StringUtils.isBlank(searchParams.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            String[] price = searchParams.getSkuPrice().split("_");
            if (price.length > 1) {
                if (searchParams.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(price[1]);
                } else {
                    rangeQuery.gte(price[0]).lte(price[1]);
                }
            } else if (price.length == 1) {
                if (searchParams.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(price[0]);
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }
        if (searchParams.getAttrs() != null && searchParams.getAttrs().size() > 0) {
            for (String attr : searchParams.getAttrs()) {
                BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
                String[] attrString = attr.split("_");
                boolQueryBuilder1.must(QueryBuilders.termQuery("attrList.id", attrString[0]));
                boolQueryBuilder1.must(QueryBuilders.termsQuery(
                        "attrList.value", attrString[1].replace
                                ("comma",",").split(":")));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery
                        ("attrList", boolQueryBuilder1, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        if (!StringUtils.isBlank(searchParams.getSort())) {
            String[] sortString = searchParams.getSort().split("_");
            searchSourceBuilder.sort(sortString[0], sortString[1].equalsIgnoreCase("asc") ?
                    SortOrder.ASC : SortOrder.DESC);
        }
        if (!StringUtils.isBlank(searchParams.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("productCombTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from((searchParams.getPageNum() - 1) * EsConst.PAGE_SIZE);
        searchSourceBuilder.size(EsConst.PAGE_SIZE);
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(100);
        brand_agg.subAggregation(AggregationBuilders.terms("brandName_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brandImg_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brand_agg);

        TermsAggregationBuilder category_agg = AggregationBuilders.terms("category_agg").field("categoryId").size(100);
        category_agg.subAggregation(AggregationBuilders.terms("category_name_agg").field("categoryName").size(1));
        searchSourceBuilder.aggregation(category_agg);

        NestedAggregationBuilder attrList_agg = AggregationBuilders.nested("attrList_agg", "attrList");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrList.id").size(100);
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrList.name").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrList.value").size(20));
        attrList_agg.subAggregation(attr_id_agg);
        searchSourceBuilder.aggregation(attrList_agg);
        System.out.println(searchSourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{
                EsConst.PRODUCT}, searchSourceBuilder);
        SearchResult searchResult = null;
        try {
            SearchResponse searchResponse = restHighLevelClient.search
                    (searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            searchResult = getSearchResultVO(searchResponse, searchParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    private SearchResult getSearchResultVO(SearchResponse searchResponse, SearchParams searchParams) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        long total = searchResponse.getHits().getTotalHits().value;
        SearchResult searchResult = new SearchResult();
        searchResult.setTotal(total);
        searchResult.setPageNum(searchParams.getPageNum());
        searchResult.setTotalPages((int) ((total % searchParams.getPageSize() == 0) ? (total / searchParams.getPageSize()) : (total / searchParams.getPageSize())));
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= searchResult.getTotalPages(); i++) {
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<EsProductComb> esProductCombList = new ArrayList<>();
        if (hits != null && hits.length > 0) {
            for (SearchHit hit : hits) {
                EsProductComb esProductComb = objectMapper.readValue(hit.getSourceAsString(), EsProductComb.class);
                if (!StringUtils.isBlank(searchParams.getKeyword())) {
                    HighlightField productCombTitle = hit.getHighlightFields().get("productCombTitle");
                    String string = productCombTitle.getFragments()[0].string();
                    esProductComb.setProductCombTitle(string);
                }
                esProductCombList.add(esProductComb);
            }
        }
        searchResult.setProducts(esProductCombList);
        List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
        ParsedStringTerms category_agg = searchResponse.getAggregations().get("category_agg");
        for (Terms.Bucket bucket : category_agg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(bucket.getKeyAsString());
            ParsedStringTerms category_name_agg = bucket.getAggregations().get("category_name_agg");
            String keyAsString = category_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(keyAsString);
            catalogVoList.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVoList);
        List<SearchResult.BrandVo> brandVoList = new ArrayList<>();
        ParsedStringTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(bucket.getKeyAsString());
            ParsedStringTerms brandImg_agg = bucket.getAggregations().get("brandImg_agg");
            if (brandImg_agg.getBuckets().size() > 0) {
                String img = brandImg_agg.getBuckets().get(0).getKeyAsString();
                brandVo.setBrandImg(img);
            }
            ParsedStringTerms brandName_agg = bucket.getAggregations().get("brandName_agg");
            String name = brandName_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(name);
            brandVoList.add(brandVo);
        }
        searchResult.setBrands(brandVoList);
        ParsedNested attrList_agg = searchResponse.getAggregations().get("attrList_agg");
        ParsedStringTerms attr_id_agg = attrList_agg.getAggregations().get("attr_id_agg");
        List<SearchResult.AttrVo> attrVoList = new ArrayList<>();
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            String id = bucket.getKeyAsString();
            attrVo.setId(id);
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setName(attrName);
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<String> valueList = attr_value_agg.getBuckets().stream()
                    .map(valueBucket -> valueBucket.getKeyAsString()).collect(Collectors.toList());
            attrVo.setValues(valueList);
            attrVoList.add(attrVo);
        }
        searchResult.setAttrs(attrVoList);
        if (searchParams.getAttrs()!=null&&searchParams.getAttrs().size()>0){
            List<SearchResult.NavVo> navVoList = searchParams.getAttrs().stream().map(attr -> {
                String[] attrSplit = attr.split("_");
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                navVo.setNavValue(attrSplit[1].replace("comma",","));
                Map<String, Object> map = new HashMap<>();
                map.put("id", attrSplit[0]);
                searchResult.getAttrIds().add(attrSplit[0]);
                try {
                    R allAttribute = productFeign.getAllAttribute(map, "0");
                    List<AttributeVo> attributeVoList = allAttribute.getData(new TypeReference<>() {
                    },true);
                    navVo.setNavName(attributeVoList.get(0).getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    navVo.setNavValue("");
                }
                String replace = replaceQueryString(searchParams, attr,"attrs");
                navVo.setLink("http://search.glmall.com/list.html?"+replace);
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVoList);
        }
        if (searchParams.getBrandId()!=null&&searchParams.getBrandId().size()>0){
            R brandByBrandIdList = productFeign.findBrandByBrandIdList(searchParams.getBrandId());
            try {
                List<ProductBrandVo> brandVoList1= brandByBrandIdList.getData(new TypeReference<>() {
                  });
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                StringBuffer stringBuffer = new StringBuffer();
                String replaceQueryString="";
                for (ProductBrandVo brandVo : brandVoList1) {
                    stringBuffer.append(brandVo.getName()+";");
                    replaceQueryString = replaceQueryString(searchParams, brandVo.getId(), "brandId");
                }
                navVo.setNavName("Brand");
                navVo.setNavValue(stringBuffer.toString());
                navVo.setLink("http://search.glmall.com/list.html?"+replaceQueryString);
                searchResult.getNavs().add(navVo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return searchResult;
    }

    private String replaceQueryString(SearchParams searchParams, String attr,String attrName) {
        String encodeAttrString = URLEncoder.encode(attr, StandardCharsets.UTF_8);
        encodeAttrString=encodeAttrString.replace("%3A",":");
       if (searchParams.getQueryString().contains("&"+attrName)){
           return searchParams.getQueryString().replace(
                   "&"+attrName+"=" + encodeAttrString, "");
       }
        return searchParams.getQueryString().replace(
                attrName+"=" + encodeAttrString, "");
    }
}
