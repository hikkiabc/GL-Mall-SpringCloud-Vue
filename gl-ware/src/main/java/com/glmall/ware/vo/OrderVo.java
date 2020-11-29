package com.glmall.ware.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class OrderVo {
    private String id;
    private String orderSn;
    private String memberId;
    private String couponId;
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date createTime=new Date();
    private String memberUsername;
    private BigDecimal totalPrice;
    private BigDecimal payPrice;
    private BigDecimal freightPrice;
    private BigDecimal promotionAmount;
    private BigDecimal integrationAmount;
    private BigDecimal couponAmount;
    private BigDecimal discountAmount;
    private Integer status;
    private String deliveryCompany;
    private String deliverySn;
    private Integer payType;
    private String receiverName;
    private BigDecimal totalReducedPrice;
}
