package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.BrandCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BrandCategoryMapper extends JpaRepository<BrandCategory,String> {
    @Query(value="select * from brand_category where brand_id=?1",nativeQuery=true)
    List<BrandCategory> findByBrandId(String id);
    List<BrandCategory> findByCategoryId(String id);


    @Modifying
    @Query(value="update brand_category set " +
            "brand_name = case when ?3 is null then brand_name else ?3 end, " +
            "category_name = case when ?4 is null then category_name else ?4 end where if(?1 is null ,1=0, brand_name=?1 )" +
            " or if(?2 is null, 1=0,category_name=?2 ) ",
            nativeQuery=true)

    Integer save(String brandName,String CategoryName,String newBrandName,String newCategoryName);
}
