package com.glmall.ware.beans;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class FareVo {
    private BigDecimal fare;
    private MemberAddressVo memberAddressVo;
}
