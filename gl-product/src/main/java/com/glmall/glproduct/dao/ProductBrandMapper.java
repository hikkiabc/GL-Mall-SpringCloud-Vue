package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductBrandMapper extends JpaRepository<ProductBrand, String> , JpaSpecificationExecutor<ProductBrand> {
}
