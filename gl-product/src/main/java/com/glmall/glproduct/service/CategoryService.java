package com.glmall.glproduct.service;

import com.glmall.glproduct.beans.ProductCategory;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<ProductCategory> getCategoryWithChildren();

    Long deleteAll(List list);

    ProductCategory save(ProductCategory productCategory);

    ProductCategory findCategoryById(String id);

    ProductCategory findById(String categoryId);

    List<ProductCategory> findLvl1Category();

    Map<String, Object> getCategoryMap();
}
