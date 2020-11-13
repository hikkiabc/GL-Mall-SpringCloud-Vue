package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.ProductComb_Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductComb_AttributeMapper extends JpaRepository<ProductComb_Attribute,String> {
    @Query(value="SELECT CONCAT(`name`,': ',`value`) FROM `product_comb_attribute`" +
            " WHERE product_comb_id= ?1",nativeQuery=true)
    List<String> getSkuSaleAttrsBySkuId(String id);
}
