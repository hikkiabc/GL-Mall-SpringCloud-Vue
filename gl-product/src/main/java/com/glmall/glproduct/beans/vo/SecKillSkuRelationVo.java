package com.glmall.glproduct.beans.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class SecKillSkuRelationVo {
    private String id;
    private String promotionId;
    private String promotionSessionId;
    private String skuId;
    private BigDecimal secKillPrice;
    private Long secKillCount;
    private String secKillLimit;
    private String secKillSort;
    private Map<String,Object> productCombVo;
    private Long startTime;
    private Long endTime;
    private String token;
}
