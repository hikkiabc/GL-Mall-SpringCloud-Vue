package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryMapper extends JpaRepository<ProductCategory,String> {
    @Modifying
    @Query(nativeQuery=true ,value="update product_category set show_status = 0" +
            " where id in ?1")
    Integer deleteAllOwn(List list);
    @Query(nativeQuery=true ,value="select * from product_category where show_status" +
            "!=0 or show_status is null")
    List <ProductCategory> findAllNotDeleted();
    ProductCategory findByName(String name);
    List<ProductCategory> findByPid(String pid);

}
