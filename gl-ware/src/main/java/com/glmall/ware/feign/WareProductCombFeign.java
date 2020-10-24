package com.glmall.ware.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("guli-product")
public interface WareProductCombFeign {
    @GetMapping("/product/combination")
    public R getAllCombination(@RequestParam Map map);
}
