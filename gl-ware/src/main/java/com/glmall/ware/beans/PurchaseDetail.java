package com.glmall.ware.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
public class PurchaseDetail {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowflakeId")
private String id;
private String purchaseId;
private String productCombId;
private String amount;
private BigDecimal productCombPrice;
private String wareId;
private String status;
}
