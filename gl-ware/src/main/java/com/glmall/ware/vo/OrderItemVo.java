package com.glmall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderItemVo {
    private String id;
    private String orderSn;
    private String skuId;
    private String skuName;
    private String skuPic;
    private BigDecimal skuPrice;
    private Integer skuQuantity;
    private String skuAttrsValues;
    private String promotionAmount;
    private String couponAmount;
    private String integrationAmount;
    private Integer giftGrowth;
    private String spuId;
    private String spuName;
    private String spuBrand;
    private String category_id;
}
