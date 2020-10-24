package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.ProductCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductCombinationMapper extends JpaRepository<ProductCombination,String>, JpaSpecificationExecutor< ProductCombination> {

    List<ProductCombination> getProductCombinationByProductId(String id);
}
