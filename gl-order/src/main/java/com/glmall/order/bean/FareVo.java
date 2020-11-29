package com.glmall.order.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
    private BigDecimal fare;
    private MemberAddressVo memberAddressVo;
}
