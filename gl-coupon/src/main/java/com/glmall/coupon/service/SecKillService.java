package com.glmall.coupon.service;

import com.glmall.coupon.beans.SecKillSession;
import com.glmall.coupon.beans.SecKillSkuRelation;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface SecKillService {
    Page<SecKillSession> getSecKillSession(Map<String, Object> params);

    Page<SecKillSkuRelation> getSecKillSkuRelation(Map<String, Object> params);

    SecKillSession getSecKillSessionById(String id);

    SecKillSkuRelation getSecKillSkuRelationById(String id);

    SecKillSession save(SecKillSession secKillSession);

    SecKillSkuRelation saveSecKillSkuRelation(SecKillSkuRelation secKillSkuRelation);

    Integer deleteSecKillSession(List<String> ids);

    Integer deleteSecKillSkuRelation(List<String> ids);


    List<SecKillSession> getLatest3DaysSecKillProducts();
}
