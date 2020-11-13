package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.AttributeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface AttributeGroupMapper extends JpaRepository<AttributeGroup, String>, JpaSpecificationExecutor<AttributeGroup> {
    List<AttributeGroup> findByCateId(String cateId);

    @Query(value = "SELECT attribute_group.name as groupName, attribute_attribute_group.attribute_id id," +
            "attribute.`name`, product_attribute.`value` FROM `attribute_group` " +
            "LEFT JOIN attribute_attribute_group on attribute_attribute_group.attribute_group_id=attribute_group.id" +
            " LEFT JOIN attribute on attribute.id=attribute_attribute_group.attribute_id" +
            " LEFT JOIN product_attribute on product_attribute.id=attribute.id" +
            " where cate_id=?1 and product_attribute.product_id=?2", nativeQuery = true)
//    @Query(value="select attribute_group.name" +
//            " from attribute_group  where cate_id=?1",nativeQuery=true)
    List<Map<String,Object>> findGroupAndAttrByCateId(String cateId, String productId);
}
