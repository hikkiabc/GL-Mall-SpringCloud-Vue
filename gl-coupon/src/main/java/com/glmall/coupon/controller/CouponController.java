package com.glmall.coupon.controller;

import com.glmall.coupon.TO.ProductDiscountTO;
import com.glmall.coupon.beans.ProductBound;
import com.glmall.coupon.service.ProductDiscountService;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/coupon")
//@CrossOrigin
@RefreshScope
public class CouponController {
@Autowired
    ProductDiscountService productDiscountService;
    @PostMapping("/product_discount")
    public R saveProduct_discount(@RequestBody ProductDiscountTO productDiscountTO){
    Map<String,Object> map= productDiscountService.saveProduct_discount(productDiscountTO);
    return R.ok().put("data",map);
    }
    @PostMapping("/product_bound")
    public R saveProductBound(@RequestBody ProductBound productBound){
       ProductBound productBound1= productDiscountService.saveProductBound(productBound);
       return R.ok().put("data",productBound1);
    }
}
