package com.glmall.order.bean;

import com.sun.jdi.PrimitiveValue;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {
    private String addrId;
    private Integer payType;
    private String orderToken;
    private BigDecimal payPrice;
    private String note;
}
