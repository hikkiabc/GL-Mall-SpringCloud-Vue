package com.glmall.order.dao;

import com.glmall.order.bean.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemEntityMapper extends JpaRepository<OrderItemEntity,String> {
    List<OrderItemEntity> findByOrderSn(String orderSn);
}
