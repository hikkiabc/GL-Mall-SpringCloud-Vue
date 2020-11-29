package com.glmall.order.config;

import com.glmall.order.bean.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqCreatedBeanConfig {

//    @RabbitListener(queues = "order.release.queue")
//    public void listen(Message message, OrderEntity orderEntity, Channel channel) throws IOException {
//        System.out.println(orderEntity);
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//
//    }

    @Bean
    public Queue orderDelayQueue() {
        Map args = new HashMap<>();
        args.put("x-dead-letter-exchange", "order-event-exchange");
        args.put("x-dead-letter-routing-key", "order.release.order");
        args.put("x-message-ttl", 10000);
        return new Queue("order.delay.queue", true, false, false, args);
    }

    @Bean
    public Queue orderReleaseQueue() {
        return new Queue("order.release.queue", true, false, false);
    }

    @Bean
    public Queue orderSecKillQueue(){
        return new Queue("order.secKill.queue",true,false,false);
    }

    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange",true,false);
    }

    @Bean
    public Binding orderCreatedBinding(){
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.created.order",null);
    }

    @Bean
    public Binding orderReleaseBinding(){
        return new Binding("order.release.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.release.order",null);
    }

    @Bean
    public Binding orderClosedBinding(){
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.close.order",null);
    }

    @Bean
    public Binding orderSecKillBinding(){
        return new Binding("order.secKill.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.secKill.order",null);
    }
}
