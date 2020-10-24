package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.AttributeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AttributeGroupMapper extends JpaRepository<AttributeGroup,String>, JpaSpecificationExecutor<AttributeGroup> {
    List<AttributeGroup> findByCateId(String cateId);
}
