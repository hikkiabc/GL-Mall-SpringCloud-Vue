package com.glmall.member.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("glmall-coupon")
public interface CouponFeign {
    @RequestMapping("/coupon/1")
    public R test();
}
