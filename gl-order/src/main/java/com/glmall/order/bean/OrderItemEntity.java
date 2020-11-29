package com.glmall.order.bean;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class OrderItemEntity {
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.glmall.utils.SnowflakeId")    private String id;
    private String orderSn;
    private String skuId;
    private String skuName;
    private String skuPic;
    private BigDecimal skuPrice;
    private Integer skuQuantity;
    private String skuAttrsValues;
    private String promotionAmount="0";
    private String couponAmount;
    private String integrationAmount;
    private Integer giftGrowth;
    private String spuId;
    private String spuName;
    private String spuBrand;
    private String category_id;
    private BigDecimal totalPrice;
}
