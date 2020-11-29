package com.glmall.order;

import com.glmall.order.bean.OrderEntity;
import com.glmall.order.bean.OrderItemEntity;
import com.glmall.order.dao.OrderItemEntityMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class OrderApplicationTests {
    @Autowired
    OrderItemEntityMapper orderItemEntityMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setPayType(1);
        rabbitTemplate.convertAndSend("order-event-exchange", "order.created.order", orderEntity);
    }
@Test
    void test(){
        String a="2020-01-01 23:33:33";

}
}
