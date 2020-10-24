package com.glmall.glproduct.beans.vo;

import com.glmall.coupon.beans.MemberPrice;
import com.glmall.coupon.beans.ProductCombDiscount;
import com.glmall.coupon.beans.ProductCombReduce;
import com.glmall.glproduct.beans.ProductCombImg;
import com.glmall.glproduct.beans.ProductComb_Attribute;
import com.glmall.glproduct.beans.Product_Attribute;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductCombVO {
    private String id;
    private String productId;
    private String name;
    private List<ProductCombImg> images;
    private List<ProductComb_Attribute> attr;
    private BigDecimal price;
    private String title;
    private String subTitle;
    private Integer saleCount;
    private Integer fullCount;
    private BigDecimal discount;
    private String countStatus;
    private String priceStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private List<MemberPrice> memberPrice;
}
