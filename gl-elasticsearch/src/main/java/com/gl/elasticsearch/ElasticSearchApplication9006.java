package com.gl.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ElasticSearchApplication9006 {

    public static void main(String[] args) {
        SpringApplication.run(ElasticSearchApplication9006.class, args);
    }

}
