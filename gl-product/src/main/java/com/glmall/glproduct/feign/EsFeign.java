package com.glmall.glproduct.feign;

import ES.EsProductComb;
import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gl-elastic-search")
public interface EsFeign {
    @PostMapping("/es/save/productComb")
    public R saveProductComb(@RequestBody List<EsProductComb> esProductCombs);
}
