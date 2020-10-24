package com.glmall.glproduct.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
public class ProductCombination {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowflakeId")
    private String id;
    private String productId;
    private String name;
    private String categoryId;
    private String brandId;
    private String defaultImg;
    private BigDecimal price;
    private String title;
    private String subTitle;
    private Integer saleCount=0;

}
