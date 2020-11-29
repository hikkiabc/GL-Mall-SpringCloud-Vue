package com.glmall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;
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
    private ProductCombVo productCombVo;
    private Long startTime;
    private Long endTime;
    private String token;
}
