package com.glmall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class LockOrderStockVo {
    private String orderSn;
    private List<OrderItemVo> orderItemList;
}
