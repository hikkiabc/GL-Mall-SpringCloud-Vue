package com.glmall.ware.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("glmall-order")
public interface OrderFeign {
    @GetMapping("/order/{orderSn}")
    public R getOrderBySn( @PathVariable String orderSn);

}
