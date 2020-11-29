package com.glmall.order.feign;

import com.glmall.order.bean.LockOrderStockVo;
import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@FeignClient("gl-ware")
public interface WareFeign {
    @PostMapping("/ware/product_comb/stock")
    public R getProductCombHasStockByProductCombIdList(@RequestBody List<String> idList);

    @GetMapping("/ware/wareinfo/fare")
    public R getDeliveryFareByAddressId(@RequestParam String addrId) throws IOException;

    @PostMapping("/ware/stock/lockOrderItemStock")
    public R lockOrderItemStock(@RequestBody LockOrderStockVo LockOrderStockVo);
}
