package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.Attribute_AttributeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface Attribute_AttributeGroupMapper extends JpaRepository<Attribute_AttributeGroup,String> , JpaSpecificationExecutor<Attribute_AttributeGroup> {
    Attribute_AttributeGroup findByAttributeId(String id);
    Integer deleteByAttributeId(String id);
    List<Attribute_AttributeGroup> findByAttributeGroupId(String groupId);
    List<Attribute_AttributeGroup> findByAttributeGroupIdIn(List<String> ids);
}
