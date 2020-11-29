package com.glmall.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

@SpringBootTest
class SeckillApplicationTests {

    @Test
    void contextLoads() {

        String regex="\\d+_"+"766782416822992896";
        System.out.println(Pattern.matches(regex, "5_766782416822992896"));
    }

}
