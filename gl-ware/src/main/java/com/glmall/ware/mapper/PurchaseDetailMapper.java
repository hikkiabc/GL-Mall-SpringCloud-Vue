package com.glmall.ware.mapper;

import com.glmall.ware.beans.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PurchaseDetailMapper extends JpaRepository<PurchaseDetail,String>, JpaSpecificationExecutor<PurchaseDetail> {
}
