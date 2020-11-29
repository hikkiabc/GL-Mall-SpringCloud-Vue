package com.glmall.member.config;

import com.glmall.member.interceptor.MemberInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MemberWebConfig implements WebMvcConfigurer {
    @Autowired
    MemberInterceptor memberInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(memberInterceptor).addPathPatterns("/**");
    }
}
