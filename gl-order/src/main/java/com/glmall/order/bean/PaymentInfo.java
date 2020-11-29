package com.glmall.order.bean;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class PaymentInfo {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private Long id;
    /**
     * 订单号（对外业务号）
     */
    private String orderSn;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 支付宝交易流水号
     */
    private String alipayTradeNo;
    /**
     * 支付总金额
     */
    private BigDecimal totalAmount;
    /**
     * 交易内容
     */
    private String subject;
    /**
     * 支付状态
     */
    private String paymentStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 确认时间
     */
    private Date confirmTime;
    /**
     * 回调内容
     */
    private String callbackContent;
    /**
     * 回调时间
     */
    private Date callbackTime;
}
