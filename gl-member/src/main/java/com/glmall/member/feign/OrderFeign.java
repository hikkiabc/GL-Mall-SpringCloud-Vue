package com.glmall.member.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("glmall-order")
public interface OrderFeign {

    @PostMapping("/getOrderWithItem")
    public R getOrderWithItem(@RequestBody Map<String, Object> params) ;
}
