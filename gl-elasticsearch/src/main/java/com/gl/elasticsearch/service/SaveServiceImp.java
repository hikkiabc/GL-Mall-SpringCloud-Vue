package com.gl.elasticsearch.service;

import ES.EsProductComb;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gl.elasticsearch.config.ElasticSearchConfig;
import com.gl.elasticsearch.consts.EsConst;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaveServiceImp implements SaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean saveProductComb(List<EsProductComb> esProductCombs) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        BulkRequest bulkRequest = new BulkRequest();
        for (EsProductComb esProductComb : esProductCombs) {
            IndexRequest indexRequest = new IndexRequest(EsConst.PRODUCT);
            indexRequest.id(esProductComb.getProductCombId());
            indexRequest.source(objectMapper.writeValueAsString(esProductComb), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        boolean hasFailures = bulkResponse.hasFailures();
        List<String> idList = Arrays.stream(bulkResponse.getItems()).map(e -> e.getId()).collect(Collectors.toList());

        return hasFailures;
    }
}
