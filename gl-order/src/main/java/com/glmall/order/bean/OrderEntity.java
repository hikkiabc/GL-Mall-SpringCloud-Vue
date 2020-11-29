package com.glmall.order.bean;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.glmall.order.utils.OrderStatusEnum;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class OrderEntity {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String orderSn;
    private String memberId;
    private String couponId;
    @Temporal(TemporalType.TIMESTAMP)
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
    private Integer status= OrderStatusEnum.NEW.getCode();
    private String deliveryCompany;
    private String deliverySn;
    private Integer payType;
    private String receiverName;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDetailAddress;
    private BigDecimal totalReducedPrice;
    @Transient
    private List<OrderItemEntity> itemEntities;

}
