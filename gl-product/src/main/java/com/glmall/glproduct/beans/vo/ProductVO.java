package com.glmall.glproduct.beans.vo;

import com.glmall.coupon.beans.ProductBound;
import com.glmall.glproduct.beans.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVO {
    private String name;
    private String id;
    private String description;
    private String categoryId;
    private String brandId;
    private BigDecimal weight;
    private String publishStatus;
    private List<String> productDescImgs;
    private List<String> productImg;
    private List <Product_Attribute> product_attributes;
    private List<ProductCombVO> productCombVOList;
    private ProductBound productBound;
}
