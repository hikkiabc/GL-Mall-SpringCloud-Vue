package com.glmall.glproduct.beans.vo;

import com.glmall.glproduct.beans.Attribute;
import com.glmall.glproduct.beans.ProductCombImg;
import com.glmall.glproduct.beans.ProductCombination;
import com.glmall.glproduct.beans.ProductDescImg;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkuItemInfoVo {
    private Boolean hasStock=true;
    private ProductCombination info;
    private List<ProductCombImg> images;
    private ProductDescImg desc;
    private List<SkuItemSaleAttrVo> saleAttrs=new ArrayList<>();
    private List<SpuGroupAndAttrVo> groupAttrs=new ArrayList<>();

}
