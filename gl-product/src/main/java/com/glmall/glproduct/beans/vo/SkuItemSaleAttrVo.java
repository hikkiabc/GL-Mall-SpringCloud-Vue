package com.glmall.glproduct.beans.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkuItemSaleAttrVo {
    private String attrId;
    private String attrName;
    private List<SaleAttrValueAndSkuIds> attrValues=new ArrayList<>();
}
