package com.glmall.coupon.TO;

import com.glmall.coupon.beans.MemberPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class ProductDiscountTO {
    private String productCombId;
    private String fullCount;
    private BigDecimal discount;
    private String countStatus;
    private String priceStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private List<MemberPrice> memberPrice;
}
