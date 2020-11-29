package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface ProductEntityMapper extends JpaRepository<ProductEntity,String>, JpaSpecificationExecutor<ProductEntity> {
    @Modifying
    @Query(value="update product_entity set publish_status=1, update_time=?2 where id=?1",nativeQuery=true)
    Integer save1(String id, Date date);

    @Query(value="select product_entity.*, product_brand.name brand_name from" +
            " product_combination RIGHT JOIN product_entity on" +
            " product_combination.product_id=product_entity.id LEFT JOIN product_brand" +
            " on product_brand.id=product_entity.brand_id where product_combination.id=?1",nativeQuery=true)
    ProductEntity getProductEntityBySkuId(String id);
}
