package com.glmall.glproduct.service;

import com.glmall.glproduct.beans.ProductCombination;
import com.glmall.glproduct.beans.ProductEntity;
import com.glmall.glproduct.beans.vo.ProductVO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ProductService {
    Map saveProduct(ProductVO productVO);

    Page<ProductEntity> getAllProduct(Map map);

    Integer updateProductById(String id);

    Page<ProductCombination> getAllCombination(Map map);
}
