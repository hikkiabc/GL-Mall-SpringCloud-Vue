package com.glmall.order.service;

import TO.SecKillOrderTo;
import com.glmall.order.bean.OrderConfirmVo;
import com.glmall.order.bean.OrderEntity;
import com.glmall.order.bean.OrderSubmitResponseVo;
import com.glmall.order.bean.OrderSubmitVo;
import com.glmall.order.vo.PayAsyncVo;
import com.glmall.order.vo.PayVo;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface OrderService {
    OrderConfirmVo confirmOrder() throws IOException, ExecutionException, InterruptedException;

    OrderSubmitResponseVo submitOrder(OrderSubmitVo orderSubmitVo) throws IOException;

    OrderEntity getOrderBySn(String sn);

    void closeOrder(OrderEntity orderEntity);

    PayVo payOrder(String orderSn) throws IOException;

    Page<OrderEntity> getOrderWithItem(String id, Map<String, Object> params);

    String handlePayAsyncNotify(PayAsyncVo parameterMap);

    void createSecKillOrder(SecKillOrderTo secKillOrderTo);
}
