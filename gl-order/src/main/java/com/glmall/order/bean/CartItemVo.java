package com.glmall.order.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemVo {
    private String skuId;
    private String title;
    private Boolean checked;
    private String  image;
    private List<String> skuAttr;
    private Integer count;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
