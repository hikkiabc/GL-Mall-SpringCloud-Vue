package com.glmall.glproduct.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Attribute {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String icon;
    private String categoryId;
    private String value;
    private String name;
    private String valueType;
    private String searchType;
    private String fastDisplay;
    private String status;
    private String type;
}
