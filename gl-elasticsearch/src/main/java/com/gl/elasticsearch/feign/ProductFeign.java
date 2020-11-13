package com.gl.elasticsearch.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("guli-product")
public interface ProductFeign {
    @GetMapping("/product/attr/attribute/{categoryId}")
    public R getAllAttribute(@RequestParam Map map, @PathVariable("categoryId") String categoryId);

    @GetMapping("/product/brand-by-brandIds")
    public R findBrandByBrandIdList(@RequestParam("brandIds") List<String> brandIds);
}
