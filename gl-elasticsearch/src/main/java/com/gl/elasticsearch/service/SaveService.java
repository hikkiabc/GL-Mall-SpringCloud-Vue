package com.gl.elasticsearch.service;

import ES.EsProductComb;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

public interface SaveService {
    Boolean saveProductComb(List<EsProductComb> esProductCombs) throws IOException;
}
