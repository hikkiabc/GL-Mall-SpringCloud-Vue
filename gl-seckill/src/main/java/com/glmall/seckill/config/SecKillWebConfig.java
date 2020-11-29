package com.glmall.seckill.config;

import com.glmall.seckill.interceptor.SecKillInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class SecKillWebConfig implements WebMvcConfigurer {
    @Autowired
    SecKillInterceptor secKillInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(secKillInterceptor).addPathPatterns("/**");
    }
}
