package com.glmall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig_1 {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue stockReleaseQueue(){
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    @Bean
    public Queue stockLockQueue(){
        Map args = new HashMap<>();
        args.put("x-dead-letter-exchange", "stock.event.exchange");
        args.put("x-dead-letter-routing-key", "stock.release");
        args.put("x-message-ttl", 20000);
        return new Queue("stock.lock.stock.queue",true,false,false,args);
    }

    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock.event.exchange",true,false);
    }

    @Bean
    public Binding stockLockBinding(){
        return new Binding("stock.lock.stock.queue", Binding.DestinationType.QUEUE,
                "stock.event.exchange","stock.locked",null);
    }

    @Bean
    public Binding stockReleaseBinding(){
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "stock.event.exchange","stock.release.#",null);
    }
}
