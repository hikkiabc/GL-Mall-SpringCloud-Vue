package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.ProductDescImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDescImgMapper extends JpaRepository<ProductDescImg,String> {
    ProductDescImg findByProductId(String id);
}
