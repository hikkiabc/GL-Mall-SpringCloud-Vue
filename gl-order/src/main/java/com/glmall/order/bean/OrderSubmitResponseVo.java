package com.glmall.order.bean;

import lombok.Data;

import java.util.List;

@Data
public class OrderSubmitResponseVo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItemEntityList;
    private Integer code;
}
