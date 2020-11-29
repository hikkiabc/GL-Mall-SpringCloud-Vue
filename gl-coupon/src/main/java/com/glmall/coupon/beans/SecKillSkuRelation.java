package com.glmall.coupon.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class SecKillSkuRelation {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String promotionId;
    private String promotionSessionId;
    private String skuId;
    private BigDecimal secKillPrice;
    private Long secKillCount;
    private String secKillLimit;
    private String secKillSort;
}
