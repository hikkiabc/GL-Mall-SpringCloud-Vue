package com.glmall.glproduct.controller;

import TO.ProductCombTo;
import com.glmall.glproduct.beans.ProductCombination;
import com.glmall.glproduct.service.SkuService;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductCombController {
    @Autowired
    SkuService skuService;

    @GetMapping("/getProductCombInfo")
    public R getProductCombByProductCombId(String id){
        ProductCombination productCombination= skuService.getProductCombByProductCombId(id);
        return R.ok().put("data",productCombination);
    }
}
