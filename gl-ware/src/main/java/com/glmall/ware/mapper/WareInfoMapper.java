package com.glmall.ware.mapper;

import com.glmall.ware.beans.WareInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WareInfoMapper extends JpaRepository<WareInfo,String >, JpaSpecificationExecutor<WareInfo> {
}
