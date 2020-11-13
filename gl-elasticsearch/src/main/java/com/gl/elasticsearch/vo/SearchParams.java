package com.gl.elasticsearch.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParams {
    private String sort;
    private String catalog3Id;
    private String name;
    private String keyword;
    private List<String> brandId;
    private Integer hasStock;
    private String skuPrice;
    private Integer pageNum=1;
    private Integer pageSize=2;
    private String queryString;
    private List<String> attrs;


}
