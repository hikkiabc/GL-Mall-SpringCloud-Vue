package com.glmall.glproduct.service;

import ES.EsProductComb;
import TO.ProductCombStockTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.glmall.coupon.TO.ProductDiscountTO;
import com.glmall.coupon.beans.ProductBound;
import com.glmall.glproduct.beans.*;
import com.glmall.glproduct.beans.vo.ProductCombVO;
import com.glmall.glproduct.beans.vo.ProductVO;
import com.glmall.glproduct.dao.*;
import com.glmall.glproduct.feign.CouponFeign;
import com.glmall.glproduct.feign.EsFeign;
import com.glmall.glproduct.feign.WareFeign;
import com.glmall.utils.R;
import com.glmall.utils.RRException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImp implements ProductService {
    @Autowired
    ProductEntityMapper productEntityMapper;
    @Autowired
    ProductCombinationMapper productCombinationMapper;
    @Autowired
    ProductImgMapper productImgMapper;
    @Autowired
    ProductDescImgMapper productDescImgMapper;
    @Autowired
    Product_AttributeMapper product_attributeMapper;
    @Autowired
    AttributeMapper attributeMapper;
    @Autowired
    ProductCombImgMapper productCombImgMapper;
    @Autowired
    ProductComb_AttributeMapper productComb_attributeMapper;
    @Autowired
    CouponFeign couponFeign;
    @Autowired
    ProductBrandMapper productBrandMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    WareFeign wareFeign;
    @Autowired
    EsFeign esFeign;

    @Override
    public Map saveProduct(ProductVO productVO) {
        Map<String, Object> map = new HashMap<>();
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productVO, productEntity);
        ProductEntity save = productEntityMapper.save(productEntity);
        ProductDescImg productDescImg = new ProductDescImg();
        productDescImg.setProductId(save.getId());
        productDescImg.setImgUrl(String.join(",", productVO.getProductDescImgs()));
        ProductDescImg productDescImg1 = productDescImgMapper.save(productDescImg);
        List<ProductImg> productImgList = productVO.getProductImg().stream().map(e -> {
            ProductImg productImg = new ProductImg();
            productImg.setProductId(save.getId());
            productImg.setImgUrl(e);
            return productImg;
        }).collect(Collectors.toList());
        List<ProductImg> productImgList1 = productImgMapper.saveAll(productImgList);
        List<Product_Attribute> product_attributeList = productVO.getProduct_attributes().stream().map(e -> {
            e.setProductId(save.getId());
            Attribute attribute = attributeMapper.findById(e.getId()).get();
            e.setName(attribute.getName());
            return e;
        }).collect(Collectors.toList());
        List<Product_Attribute> product_attributeList1 = product_attributeMapper.saveAll(product_attributeList);
        ProductBound productBound = productVO.getProductBound();
        productBound.setProductId(save.getId());
        R saveProductBound = couponFeign.saveProductBound(productBound);
        map.put("saveProductBound", saveProductBound);
        map.put("product_attributeList1", product_attributeList1);
        map.put("productImgList1", productImgList1);
        map.put("productDescImg1", productDescImg1);
        map.put("productBase", save);
        List<ProductCombVO> productCombVOList = productVO.getProductCombVOList();
        List savedProductCombList = new ArrayList();
        productCombVOList.forEach(e -> {
            Map<String, Object> savedProductCombItem = new HashMap<>();
            ProductCombination productCombination = new ProductCombination();
            BeanUtils.copyProperties(e, productCombination);
            productCombination.setProductId(save.getId());
            productCombination.setCategoryId(productVO.getCategoryId());
            productCombination.setBrandId(productVO.getBrandId());
            List<ProductCombImg> images = e.getImages();
            for (ProductCombImg image : images) {
                if (image.getDefaultImg().equals("1")) {
                    productCombination.setDefaultImg(image.getImgUrl());
                }
            }
            ProductCombination savedProductComb = productCombinationMapper.save(productCombination);

            List<ProductCombImg> productCombImgList = images.stream().map(e1 -> {
                e1.setProductCombinationId(savedProductComb.getId());
                return e1;
            }).filter(e11 -> !StringUtils.isEmpty(e11.getImgUrl())).collect(Collectors.toList());
            List<ProductCombImg> productCombImgs = productCombImgMapper.saveAll(productCombImgList);

            List<ProductComb_Attribute> attr = e.getAttr();
            List<ProductComb_Attribute> productCombAttributeList = attr.stream().map(attrItem -> {
                attrItem.setProductCombId(savedProductComb.getId());
                return attrItem;
            }).collect(Collectors.toList());
            List<ProductComb_Attribute> productComb_attributeList = productComb_attributeMapper.saveAll(productCombAttributeList);
            savedProductCombItem.put("savedProductComb", savedProductComb);
            savedProductCombItem.put("productCombImgs", productCombImgs);
            savedProductCombItem.put("productComb_attributeList", productComb_attributeList);
            ProductDiscountTO productDiscountTO = new ProductDiscountTO();
            BeanUtils.copyProperties(e, productDiscountTO);
            productDiscountTO.setProductCombId(savedProductComb.getId());
            R saveProduct_discount = couponFeign.saveProduct_discount(productDiscountTO);
            savedProductCombItem.put("saveProduct_discount", saveProduct_discount);
            savedProductCombList.add(savedProductCombItem);
        });
        map.put("savedProductCombList", savedProductCombList);
        return map;
    }

    @Override
    public Page<ProductEntity> getAllProduct(Map map) {
        Specification<ProductEntity> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<ProductEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                String key = (String) map.get("key");
                String status = (String) map.get("status");
                String categoryId = (String) map.get("categoryId");
                String brandId = (String) map.get("brandId");
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isBlank(key)) {
                    Predicate name = criteriaBuilder.like(root.get("name"), "%" + key + "%");
                    Predicate id = criteriaBuilder.equal(root.get("id"), key);
                    Predicate or = criteriaBuilder.or(name, id);
                    predicates.add(or);
                }
                if (!StringUtils.isBlank(categoryId)) {
                    predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
                }
                if (!StringUtils.isBlank(brandId)) {
                    predicates.add(criteriaBuilder.equal(root.get("brandId"), brandId));
                }
                if (!StringUtils.isBlank(status)) {
                    predicates.add(criteriaBuilder.equal(root.get("publishStatus"), status));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        Page<ProductEntity> productEntityPage = productEntityMapper.findAll(specification,
                PageRequest.of(Integer.valueOf((String) map.get("pageNum")) - 1,
                        Integer.valueOf((String) map.get("pageSize"))));
        return productEntityPage;
    }

    @Override
    public Integer updateProductById(String id) {

        List<Product_Attribute> product_attributes = product_attributeMapper.findByProductId(id);
        List<String> idList = product_attributes.stream().map(Product_Attribute::getId).collect(Collectors.toList());
        List<String> searchIdList = attributeMapper.findSearchTypeByIds(idList);
        Set<String> idSet = new HashSet<>(searchIdList);
        List<EsProductComb.Attr> attrListEs = product_attributes.stream().filter(e -> idSet.contains(e.getId())).map(e1 -> {
            EsProductComb.Attr attr = new EsProductComb.Attr();
            BeanUtils.copyProperties(e1, attr);
            return attr;
        }).collect(Collectors.toList());
        List<ProductCombination> productCombinationByProductId = productCombinationMapper.getProductCombinationByProductId(id);
        List<String> productCombIdList = productCombinationByProductId.stream()
                .map(ProductCombination::getId).collect(Collectors.toList());
        Map<String, Boolean> hasStockMap = null;
        try {
            R productCombStockTOList =
                    wareFeign.getProductCombHasStockByProductCombIdList(productCombIdList);
            hasStockMap = productCombStockTOList.getData(new TypeReference<List<ProductCombStockTO>>() {
            }).stream().collect(Collectors
                    .toMap(ProductCombStockTO::getProductCombId, ProductCombStockTO::getHasStock));
        } catch (Exception e) {
            e.printStackTrace();

        }
        Map<String, Boolean> finalHasStockMap = hasStockMap;
        List<EsProductComb> esProductCombList = productCombinationByProductId.stream().map(e -> {
            EsProductComb esProductComb = new EsProductComb();
            BeanUtils.copyProperties(e, esProductComb);
            esProductComb.setProductCombId(e.getId());
            esProductComb.setProductId(id);
            esProductComb.setProductCombImg(e.getDefaultImg());
            esProductComb.setProductCombTitle(e.getTitle());
            ProductCategory productCategory = categoryMapper.findById(e.getCategoryId()).get();
            esProductComb.setCategoryName(productCategory.getName());
            ProductBrand productBrand = productBrandMapper.findById(e.getBrandId()).get();
            esProductComb.setBrandName(productBrand.getName());
            if (finalHasStockMap == null) {
                esProductComb.setHasStock(true);
            } else {
                System.out.println(finalHasStockMap.get(e.getId()));
                esProductComb.setHasStock(finalHasStockMap.get(e.getId()));
            }
            esProductComb.setAttrList(attrListEs);
            return esProductComb;
        }).collect(Collectors.toList());
        R r = esFeign.saveProductComb(esProductCombList);
        Integer productEntity=0;
        if ((Integer) r.get("code")==0){
             productEntity = productEntityMapper.save1(id,new Date());
        }else{
        }
        return productEntity;
    }


    @Override
    public Page<ProductCombination> getAllCombination(Map map) {
        Specification<ProductCombination> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<ProductCombination> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                String key = (String) map.get("key");
                String min = (String) map.get("min");
                String max = (String) map.get("max");
                String categoryId = (String) map.get("categoryId");
                String brandId = (String) map.get("brandId");
                if (!StringUtils.isBlank(key)) {
                    Predicate name = criteriaBuilder.like(root.get("name"), "%" + key + "%");
                    Predicate id = criteriaBuilder.equal(root.get("id"), key);
                    Predicate or = criteriaBuilder.or(name, id);
                    predicates.add(or);
                }
                if (!StringUtils.isBlank(categoryId)) {
                    predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
                }
                if (!StringUtils.isBlank(brandId)) {
                    predicates.add(criteriaBuilder.equal(root.get("brandId"), brandId));
                }
                if (!StringUtils.isBlank(max) && new BigDecimal(max).compareTo(new BigDecimal("0")) > 0) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), max));
                }
                if (!StringUtils.isBlank(min)) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), min));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        Page<ProductCombination> productCombinationPage = productCombinationMapper.findAll(specification, PageRequest.of(Integer.valueOf((String) map.get("pageNum")) - 1,
                Integer.valueOf((String) map.get("pageSize"))));
        return productCombinationPage;
    }
}
