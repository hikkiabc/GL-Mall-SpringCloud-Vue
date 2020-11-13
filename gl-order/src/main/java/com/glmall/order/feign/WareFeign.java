package com.glmall.order.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gl-ware")
public interface WareFeign {
    @PostMapping("/ware/product_comb/stock")
    public R getProductCombHasStockByProductCombIdList(@RequestBody List<String> idList);
}
