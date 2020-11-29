package com.glmall.order.dao;

import com.glmall.order.bean.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderEntityMapper extends JpaRepository<OrderEntity, String> {
    OrderEntity findByOrderSn(String sn);

    @Modifying
    @Query("update OrderEntity set status=4 where id=:#{#orderEntity.id}")
    void closeOrder(@Param("orderEntity") OrderEntity orderEntity);

    @Query(value = "select  * from order_entity where id= ?1", nativeQuery = true)
    OrderEntity findById_1(String id);

    @Query(value = "select * from order_entity where member_id=?1", nativeQuery = true)
    Page<OrderEntity> findByMemberId(String memberId, PageRequest page);

    @Modifying
    @Query(value = "update order_entity set status=?2 where order_sn=?1", nativeQuery = true)
    void updateOrderStatus(String orderSn, Integer code);
}
