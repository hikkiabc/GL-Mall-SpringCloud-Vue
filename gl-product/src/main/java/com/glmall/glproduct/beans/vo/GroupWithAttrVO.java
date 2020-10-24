package com.glmall.glproduct.beans.vo;

import com.glmall.glproduct.beans.Attribute;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class GroupWithAttrVO {
    private String id;
    private String name;
    private String cateId;
    private String icon;
    private String sort;
    private String description;
    private List categoryIds;
    private List<Attribute> attributeList;
}
