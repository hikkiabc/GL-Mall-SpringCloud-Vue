package com.glmall.glproduct.feign;

import TO.ProductCombStockTO;
import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("gl-ware")
public interface WareFeign {
    @PostMapping("/ware/product_comb/stock")
    public R getProductCombHasStockByProductCombIdList(@RequestBody List<String> idList);
}
