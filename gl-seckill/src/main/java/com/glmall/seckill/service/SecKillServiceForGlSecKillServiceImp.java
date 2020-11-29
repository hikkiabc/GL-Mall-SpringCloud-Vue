package com.glmall.seckill.service;

import TO.MemberTo;
import TO.SecKillOrderTo;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glmall.seckill.feign.CouponFeign;
import com.glmall.seckill.feign.ProductFeign;
import com.glmall.seckill.interceptor.SecKillInterceptor;
import com.glmall.seckill.vo.ProductCombVo;
import com.glmall.seckill.vo.SecKillSessionVo;
import com.glmall.seckill.vo.SecKillSkuRelationVo;
import com.glmall.utils.R;
import com.glmall.utils.SnowFlakeWorker;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Transactional
@Service
public class SecKillServiceForGlSecKillServiceImp implements SecKillServiceForGlSeckKill {
    @Autowired
    CouponFeign couponFeign;
    @Autowired
    ProductFeign productFeign;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    private final String REDIS_SECKILLSESSION_PREFIX = "secKill:session:";
    private final String REDIS_SECKILLSKU_PREFIX = "secKill:sku:";
    private final String REDISSON_SEMAPHORE_PREFIX = "secKill:stock:";
    private final String REDIS_USERSECKILL_PREFIX = "secKill:userSecKill:";


    @Override
    public void listLatest3DaysSecKillProducts() throws IOException {
        R latest3DaysSecKillProducts = couponFeign.getLatest3DaysSecKillProducts();
        List<SecKillSessionVo> secKillSessionVos = latest3DaysSecKillProducts.getData(new TypeReference<>() {
        });
        ObjectMapper objectMapper = new ObjectMapper();
        if (secKillSessionVos.size() > 0) {
            secKillSessionVos.stream().forEach(secKillSessionVo -> {
                String key = REDIS_SECKILLSESSION_PREFIX
                        + secKillSessionVo.getStartTime().getTime() + "_" + secKillSessionVo.getEndTime().getTime();
                if (!stringRedisTemplate.hasKey(key)) {
                    BoundHashOperations<String, Object, Object> boundHashOperations
                            = stringRedisTemplate.boundHashOps(REDIS_SECKILLSKU_PREFIX);
                    List<String> skuRelationIds = secKillSessionVo.getSecKillSkuRelationList().stream()
                            .map(secKillSkuRelationVo -> {
                                R productCombByProductCombId =
                                        productFeign.getProductCombByProductCombId(secKillSkuRelationVo.getSkuId());
                                try {
                                    ProductCombVo productCombVo =
                                            productCombByProductCombId.getData(new TypeReference<>() {
                                            });

                                    if (!boundHashOperations.hasKey(secKillSkuRelationVo.getPromotionSessionId() +
                                            "_" + secKillSkuRelationVo.getSkuId())) {
                                        secKillSkuRelationVo.setProductCombVo(productCombVo);
                                        secKillSkuRelationVo.setStartTime(secKillSessionVo.getStartTime().getTime());
                                        secKillSkuRelationVo.setEndTime(secKillSessionVo.getEndTime().getTime());
                                        String uuid = UUID.randomUUID().toString();
                                        secKillSkuRelationVo.setToken(uuid);
                                        String valueAsString = objectMapper.writeValueAsString(secKillSkuRelationVo);
                                        //distribute lock for seckill stock need a random key for on-sec products
                                        RSemaphore stockSemaphore =
                                                redissonClient.getSemaphore(REDISSON_SEMAPHORE_PREFIX + uuid);
                                        stockSemaphore.trySetPermits(secKillSkuRelationVo.getSecKillCount().intValue());

                                        boundHashOperations.put(secKillSkuRelationVo.getPromotionSessionId() +
                                                "_" + secKillSkuRelationVo.getSkuId(), valueAsString);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                return secKillSkuRelationVo.getPromotionSessionId() + "_" + secKillSkuRelationVo.getSkuId();
                            }).collect(Collectors.toList());
                    stringRedisTemplate.opsForList().leftPushAll(key, skuRelationIds);
                }
            });
        }
    }


    public List<SecKillSkuRelationVo> blockHandler(BlockException e) {
        System.out.println("block handle");
        return null;
    }

    @SentinelResource(value = "getCurrentSecKillSkus", blockHandler = "blockHandler")
    @Override
    public List<SecKillSkuRelationVo> getCurrentSecKillSkus() {
        List<SecKillSkuRelationVo> resList = new ArrayList<>();
        long nowTime = new Date().getTime();
        ObjectMapper objectMapper = new ObjectMapper();
        BoundHashOperations<String, String, String> hashOps =
                stringRedisTemplate.boundHashOps(REDIS_SECKILLSKU_PREFIX);
        Set<String> keys = stringRedisTemplate.keys(REDIS_SECKILLSESSION_PREFIX + "*");
        for (String key : keys) {
            String replace = key.replace(REDIS_SECKILLSESSION_PREFIX, "");
            String[] s = replace.split("_");
            if (nowTime >= Long.parseLong(s[0]) && nowTime <= Long.parseLong(s[1])) {
                List<String> range = stringRedisTemplate.opsForList().range(key, -100, 100);
                List<String> stringList = hashOps.multiGet(range);
                List<SecKillSkuRelationVo> secKillSkuRelationVoList = stringList.stream().map(sku -> {
                    try {
                        return objectMapper.readValue(sku, SecKillSkuRelationVo.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());
                resList.addAll(secKillSkuRelationVoList);
            }
        }
        return resList;
    }

    @Override
    public SecKillSkuRelationVo getSkuSecKillInfo(String skuId) throws JsonProcessingException {
        BoundHashOperations<String, String, String> hashOps =
                stringRedisTemplate.boundHashOps(REDIS_SECKILLSKU_PREFIX);
        Set<String> keys = hashOps.keys();
        long now = new Date().getTime();
        ObjectMapper objectMapper = new ObjectMapper();
        String regex = "\\d+_" + skuId;
        for (String key : keys) {
            if (Pattern.matches(regex, key)) {
                String s = hashOps.get(key);
                SecKillSkuRelationVo secKillSkuRelationVo = objectMapper.readValue(s, SecKillSkuRelationVo.class);
                if (secKillSkuRelationVo.getStartTime() < now && secKillSkuRelationVo.getEndTime() > now) {

                } else {
                    secKillSkuRelationVo.setToken(null);
                }
                return secKillSkuRelationVo;
            }
        }
        return null;
    }

    @Override
    public String secKill(String killId, String key, Integer num) throws JsonProcessingException {
        MemberTo memberTo = SecKillInterceptor.threadLocal.get();
        SnowFlakeWorker snowFlakeWorker = new SnowFlakeWorker();
        BoundHashOperations<String, String, String> hashOps =
                stringRedisTemplate.boundHashOps(REDIS_SECKILLSKU_PREFIX);
        String s = hashOps.get(killId);
        if (StringUtils.isBlank(s)) return null;
        ObjectMapper objectMapper = new ObjectMapper();
        SecKillSkuRelationVo secKillSkuRelationVo = objectMapper.readValue(s, SecKillSkuRelationVo.class);
        Long endTime = secKillSkuRelationVo.getEndTime();
        Long startTime = secKillSkuRelationVo.getStartTime();
        long now = new Date().getTime();
        if (now >= startTime && now <= endTime) {
            String token = secKillSkuRelationVo.getToken();
            String skuId = secKillSkuRelationVo.getSkuId();
            String promotionSessionId = secKillSkuRelationVo.getPromotionSessionId();
            if (token.equals(key) && (promotionSessionId + "_" + skuId).equals(killId)) {
                if (num <= Integer.parseInt(secKillSkuRelationVo.getSecKillLimit())) {
                    String userHasBoughtKey = REDIS_USERSECKILL_PREFIX + "_" + memberTo.getId() + "_" + killId;
                    Boolean setIfAbsent = stringRedisTemplate.opsForValue().
                            setIfAbsent(userHasBoughtKey, num.toString(), endTime - now, TimeUnit.MILLISECONDS);
                    if (setIfAbsent) {
                        RSemaphore semaphore = redissonClient.getSemaphore(REDISSON_SEMAPHORE_PREFIX + token);
                        try {
                            boolean b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                            if (b) {
                                String orderSn = String.valueOf(snowFlakeWorker.nextId());
                                SecKillOrderTo secKillOrderTo = new SecKillOrderTo();
                                secKillOrderTo.setNum(num);
                                secKillOrderTo.setUserId(memberTo.getId());
                                secKillOrderTo.setSeckillPrice(secKillSkuRelationVo.getSecKillPrice());
                                secKillOrderTo.setOrderSn(orderSn);
                                secKillOrderTo.setPromotionSessionId(secKillSkuRelationVo.getPromotionSessionId());
                                secKillOrderTo.setSkuId(secKillSkuRelationVo.getSkuId());
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.secKill.order", secKillOrderTo);
                                return orderSn;
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }


}
