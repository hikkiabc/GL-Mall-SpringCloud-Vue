package com.gl.elasticsearch.vo;

import lombok.Data;

import java.util.List;
@Data
public class AttributeVo {
    private String id;
    private String icon;
    private String categoryId;
    private String groupId;
    private String value;
    private String name;
    private String valueType;
    private String searchType;
    private String fastDisplay;
    private String status;
    private String type;
    private String groupName;
    private String categoryName;
    private List<String> categoryIds;
}
