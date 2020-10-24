package com.glmall.glproduct.service;

import com.glmall.glproduct.beans.BrandCategory;
import com.glmall.glproduct.beans.ProductBrand;
import com.glmall.glproduct.beans.ProductCategory;
import com.glmall.glproduct.dao.BrandCategoryMapper;
import com.glmall.glproduct.dao.ProductBrandMapper;
import com.glmall.utils.UpdateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductBrandServiceImp implements ProductBrandService {
    @Autowired
    ProductBrandMapper productBrandMapper;
    @Autowired
    BrandCategoryMapper brandCategoryMapper;
    @Autowired
    CategoryService categoryService;

    @Override
    public Page<ProductBrand> findAllBrand(Map map) {
        PageRequest of;
        String cateId = (String) map.get("cateId");
        Specification<ProductBrand> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<ProductBrand> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                String key = (String) map.get("key");

                List<Predicate> predicates = new ArrayList<>();
                if(!org.apache.commons.lang.StringUtils.isBlank(cateId)){
                    List<BrandCategory> byCategoryId = brandCategoryMapper.findByCategoryId(cateId);
                    List<String> brandIds = byCategoryId.stream().map(BrandCategory::getBrandId).collect(Collectors.toList());
                    CriteriaBuilder.In<Object> id = criteriaBuilder.in(root.get("id")).value(brandIds);
                    predicates.add(id);
                }
                if (!org.apache.commons.lang.StringUtils.isBlank(key)) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + key + "%"));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();

            }
        };
        if (!org.apache.commons.lang.StringUtils.isBlank(cateId)){
          of=PageRequest.of(0,100000);
        }  else{
            of = PageRequest.of(Integer.valueOf(map.get("pageNum").toString()) - 1, Integer.valueOf(map.get("pageSize").toString()));
        }
        return productBrandMapper.findAll(specification, of);
    }

    @Override
    public ProductBrand save(ProductBrand productBrand) {
        System.out.println(productBrand);
        if (!StringUtils.isEmpty(productBrand.getId())) {

            ProductBrand productBrand1 = productBrandMapper.findById(productBrand.getId()).get();
            if (!org.apache.commons.lang.StringUtils.isBlank(productBrand.getName())) {
                brandCategoryMapper.save(productBrand1.getName(), null, productBrand.getName(), null);
            }
            UpdateTool.copyNullProperties(productBrand1, productBrand);
        }

        return productBrandMapper.save(productBrand);
    }

    @Override
    public List<BrandCategory> getBrandCategoryRelations(String id) {
        List<BrandCategory> byBrandId = brandCategoryMapper.findByBrandId(id);
        return byBrandId;
    }

    @Override
    public BrandCategory saveOrUpdateBrandCategoryRelations(BrandCategory brandCategory) {
        Example example = Example.of(brandCategory);
        List brandMapperAll = productBrandMapper.findAll(example);

        if (brandMapperAll.size() > 0) {
            return null;
        }
        ProductCategory productCategory = categoryService.findById(brandCategory.getCategoryId());
        ProductBrand productBrand = productBrandMapper.findById(brandCategory.getBrandId()).get();
        brandCategory.setBrandName(productBrand.getName());
        brandCategory.setCategoryName(productCategory.getName());
        return brandCategoryMapper.save(brandCategory);
    }

    @Override
    public void deleteBrandCategory(BrandCategory productBrand) {
        brandCategoryMapper.delete(productBrand);
//        int i=1/0;
    }

    @Override
    public ProductBrand findById(String id) {
        ProductBrand productBrand = productBrandMapper.findById(id).get();
        return productBrand;
    }
}
