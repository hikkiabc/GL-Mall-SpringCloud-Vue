package com.glmall.ware.service;

import com.glmall.ware.beans.PurchaseEntity;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface PurchaseEntityService {
    Page<PurchaseEntity> getAllPurchase(Map map, String id);

    PurchaseEntity savePurchaseEntity(PurchaseEntity purchaseEntity);

    Map<String, Object> mergePurchaseDetail(Map<String, Object> map);
}
