package com.glmall.glproduct.feign;

import com.glmall.coupon.TO.ProductDiscountTO;
import com.glmall.coupon.beans.ProductBound;
import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("glmall-coupon")
public interface CouponFeign {
    @PostMapping("/coupon/product_discount")
    public R saveProduct_discount(@RequestBody ProductDiscountTO productDiscountTO) ;
    @PostMapping("/coupon/product_bound")
    public R saveProductBound(@RequestBody ProductBound productBound);
}
