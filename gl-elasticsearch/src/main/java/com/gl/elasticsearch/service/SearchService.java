package com.gl.elasticsearch.service;

import com.gl.elasticsearch.vo.SearchParams;
import com.gl.elasticsearch.vo.SearchResult;

public interface SearchService {
    SearchResult search(SearchParams searchParams);
}
