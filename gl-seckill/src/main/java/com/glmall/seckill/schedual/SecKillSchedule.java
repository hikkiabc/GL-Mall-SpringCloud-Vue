package com.glmall.seckill.schedual;

import com.glmall.seckill.service.SecKillServiceForGlSeckKill;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class SecKillSchedule {
    @Autowired
    SecKillServiceForGlSeckKill secKillServiceForGlSeckKill;
    @Autowired
    RedissonClient redissonClient;

    private final String SECKILL_LOCK="secKill:lock:";

    @Scheduled(cron = "0 0 3 * * ?")
    public void listLatest3DaysSecKillProducts() throws IOException {
        System.out.println("sche");
        RLock lock = redissonClient.getLock(SECKILL_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            secKillServiceForGlSeckKill.listLatest3DaysSecKillProducts();
        } finally {
            lock.unlock();
        }
    }

}
