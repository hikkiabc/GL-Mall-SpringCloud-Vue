package com.glmall.glproduct.service;

import com.glmall.glproduct.beans.Attribute;
import com.glmall.glproduct.beans.AttributeGroup;
import com.glmall.glproduct.beans.Attribute_AttributeGroup;
import com.glmall.glproduct.beans.Product_Attribute;
import com.glmall.glproduct.beans.vo.AttributeVO;
import com.glmall.glproduct.beans.vo.GroupWithAttrVO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface AttributeService {
    Page getAllAttrGroup(Map map, String cateId);

    AttributeGroup save(AttributeGroup attributeGroup);

    Map save(AttributeVO attribute);

    Page<Attribute> getAllAttribute(Map map, String categoryId);

    Integer deleteAttribute_attributeGroup(List<Attribute_AttributeGroup> attribute_attributeGroupList);

    Page<AttributeVO> getUnrelatedAttrByGroupId(Map map);

    List<Attribute_AttributeGroup> saveAttribute_attributeGroup(List<Attribute_AttributeGroup> attribute_attributeGroupList);

    List<GroupWithAttrVO> getGroupWithAttr(String categoryId);

    List<Product_Attribute> getProductBasicAttr(String categoryId);

    List<Product_Attribute> saveProductBasicAttr(List<Product_Attribute> product_attributes, String productId);
}
