package com.glmall.order.listener;

import TO.SecKillOrderTo;
import com.glmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "order.secKill.queue")
public class OrderSecKillListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void secKillOrderListen(SecKillOrderTo secKillOrderTo, Message message, Channel channel) throws IOException {

        try {
            orderService.createSecKillOrder(secKillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }

    }
}
