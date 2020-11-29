package com.glmall.order.listener;

import com.glmall.order.bean.OrderEntity;
import com.glmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "order.release.queue")
public class OrderReleaseListener {
    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void orderReleaseHandler(OrderEntity orderEntity, Message message, Channel channel) throws IOException {

        try {
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }
}
