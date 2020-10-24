package com.gl.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gl.elasticsearch.config.ElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ElasticSearchApplication9006Tests {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void esIndex() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        User user = new User();
        user.setAge(10);
        user.setUsername("username1 wuj");

        String s = objectMapper.writeValueAsString(user);
        indexRequest.source(s, XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    @Test
    void esSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("users");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("username", "username1"));
        TermsAggregationBuilder usernameAggr = AggregationBuilders
                .terms("usernameAggr").field("age").size(10);
        searchSourceBuilder.aggregation(usernameAggr);
        AvgAggregationBuilder ageAggr = AggregationBuilders.avg("ageAggr").field("age");
        searchSourceBuilder.aggregation(ageAggr);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        SearchHit[] hits = searchResponse.getHits().getHits();
//        System.out.println(searchResponse);
        for (SearchHit hit : hits) {
            User user = objectMapper.readValue(hit.getSourceAsString(), User.class);
            System.out.println(user);
        }
        Aggregations aggregations = searchResponse.getAggregations();
        Avg ageAggr1 = aggregations.get("ageAggr");
        Terms usernameAggr1 = aggregations.get("usernameAggr");
        System.out.println(ageAggr1.getValue());
        for (Terms.Bucket bucket : usernameAggr1.getBuckets()) {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
        }
    }

    @Data
    public static class User {
        private String username;
        private Integer age;
    }

}
