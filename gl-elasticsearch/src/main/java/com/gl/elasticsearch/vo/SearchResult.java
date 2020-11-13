package com.gl.elasticsearch.vo;

import ES.EsProductComb;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResult {
    private List<EsProductComb> products;
    private Integer pageNum;
    private Long total;
    private Integer totalPages;
    private List<Integer> pageNavs;
    private List<BrandVo> brands;
    private List<AttrVo> attrs;
    private List<CatalogVo> catalogs;
    private List<NavVo> navs=new ArrayList<>();
    private List<String> attrIds=new ArrayList<>();

    @Data
    public static class NavVo{
    private String navName;
    private String navValue;
    private String link;
    }

    @Data
    public static class BrandVo {
        private String brandId;
        private String brandName;
        private String brandImg;
    }
    @Data
    public static class AttrVo{
        private String name;
        private String id;
        private List<String> values;
    }
    @Data
    public static class CatalogVo{
        private String catalogId;
        private String catalogName;
    }
}
