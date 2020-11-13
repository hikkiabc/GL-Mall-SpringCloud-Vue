package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.ProductCombImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCombImgMapper extends JpaRepository<ProductCombImg,String> {
  List<ProductCombImg> findByProductCombinationId(String id);
}
