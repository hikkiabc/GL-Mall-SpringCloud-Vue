package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.Product_Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Product_AttributeMapper extends JpaRepository<Product_Attribute,String> {
    List<Product_Attribute> findByProductId(String id);
    Integer deleteByProductId(String productId);
}
