package com.glmall.glproduct.service;

import TO.ProductCombTo;
import com.glmall.glproduct.beans.ProductCombination;
import com.glmall.glproduct.beans.vo.SkuItemInfoVo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface SkuService {
    SkuItemInfoVo findSkuItemInfo(String productCombId) throws ExecutionException, InterruptedException, IOException;

    ProductCombination getProductCombByProductCombId(String id);
}
