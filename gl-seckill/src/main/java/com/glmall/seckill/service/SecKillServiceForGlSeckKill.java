package com.glmall.seckill.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glmall.seckill.vo.SecKillSkuRelationVo;

import java.io.IOException;
import java.util.List;

public interface SecKillServiceForGlSeckKill {
    void listLatest3DaysSecKillProducts() throws IOException;

    List<SecKillSkuRelationVo> getCurrentSecKillSkus();

    SecKillSkuRelationVo getSkuSecKillInfo(String skuId) throws JsonProcessingException;

    String secKill(String killId, String key, Integer num) throws JsonProcessingException;
}
