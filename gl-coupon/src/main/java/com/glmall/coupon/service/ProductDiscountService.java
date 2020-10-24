package com.glmall.coupon.service;

import com.glmall.coupon.TO.ProductDiscountTO;
import com.glmall.coupon.beans.ProductBound;

import java.util.Map;

public interface ProductDiscountService {
    Map<String, Object> saveProduct_discount(ProductDiscountTO productDiscountTO);

    ProductBound saveProductBound(ProductBound productBound);
}
