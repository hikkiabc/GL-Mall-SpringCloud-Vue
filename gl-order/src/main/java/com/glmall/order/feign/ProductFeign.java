package com.glmall.order.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("guli-product")
public interface ProductFeign {
    @GetMapping("/product/productBySkuId")
    public R getProductBySkuId(@RequestParam String id);
}
