package com.glmall.glproduct.service;

import com.glmall.glproduct.beans.BrandCategory;
import com.glmall.glproduct.beans.ProductBrand;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ProductBrandService {
    Page<ProductBrand> findAllBrand(Map map);

    ProductBrand save(ProductBrand productBrand);

    List<BrandCategory> getBrandCategoryRelations(String id);

    BrandCategory saveOrUpdateBrandCategoryRelations(BrandCategory brandCategory);

    void deleteBrandCategory(BrandCategory brandCategory);

    ProductBrand findById(String id);

    List<ProductBrand> findBrandByBrandIdList(List<String> brandIds);
}
