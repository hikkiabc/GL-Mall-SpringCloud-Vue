package com.glmall.glproduct.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class ProductCategory implements Serializable {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String pid;
    private String name;
    private String cateLevel;
    private Integer sort;
    private String showStatus="1";
    private Long count;
    private String unit;
    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List children = new ArrayList<>();
}
