package com.glmall.glproduct.feign;

import com.glmall.glproduct.feign.fallback.SecKillFeignFallBack;
import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "gl-seckill",fallback = SecKillFeignFallBack.class)
public interface SecKillFeign {

    @GetMapping("/getSkuSecKillInfo")
    public R getSkuSecKillInfo(@RequestParam String skuId);
}
