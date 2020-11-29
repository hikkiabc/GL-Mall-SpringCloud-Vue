package com.glmall.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
@EnableRabbit
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class WareApplication9005 {

    public static void main(String[] args) {
        SpringApplication.run(WareApplication9005.class, args);
    }

}
