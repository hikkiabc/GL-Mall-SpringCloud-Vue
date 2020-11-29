package com.glmall.seckill.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("glmall-coupon")
public interface CouponFeign {

    @GetMapping("/coupon/latest3DaysSecKillProducts")
    public R getLatest3DaysSecKillProducts();
}
