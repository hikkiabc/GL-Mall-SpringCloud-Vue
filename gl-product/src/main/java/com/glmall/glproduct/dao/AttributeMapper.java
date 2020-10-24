package com.glmall.glproduct.dao;

import com.glmall.glproduct.beans.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttributeMapper extends JpaRepository<Attribute,String>, JpaSpecificationExecutor<Attribute> {

    @Query(value="select id from attribute where id in ?1 and search_type = 1",nativeQuery=true)
    List<String> findSearchTypeByIds(List<String> idList);
}
