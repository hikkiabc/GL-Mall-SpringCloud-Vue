package com.glmall.ware.mapper;

import com.glmall.ware.beans.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PurchaseEntityMapper extends JpaRepository<PurchaseEntity,String>,JpaSpecificationExecutor<PurchaseEntity> {
}
