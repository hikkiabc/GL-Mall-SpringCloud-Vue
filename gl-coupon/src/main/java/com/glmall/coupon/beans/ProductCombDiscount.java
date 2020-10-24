package com.glmall.coupon.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class ProductCombDiscount {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowflakeId")
private String id;
    private String productCombId;
    private String fullCount;
    private BigDecimal discount;
    private BigDecimal price;
    private String countStatus;

}
