package com.glmall.order.bean;

import lombok.Data;

import java.util.List;

@Data
public class LockOrderStockVo {
    private String orderSn;
    private List<OrderItemEntity> orderItemList;
}
