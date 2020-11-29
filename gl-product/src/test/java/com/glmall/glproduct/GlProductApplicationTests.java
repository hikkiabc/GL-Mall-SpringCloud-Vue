package com.glmall.glproduct;

import com.glmall.glproduct.beans.ProductImg;
import com.glmall.glproduct.beans.vo.SkuItemSaleAttrVo;
import com.glmall.glproduct.dao.ProductImgMapper;
import com.glmall.utils.BeanMapUtils;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GlProductApplicationTests {
@Autowired
    RedissonClient redissonClient;
@Autowired
    ProductImgMapper productImgMapper;
    @Test
    void contextLoads() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("attr_id","1");
        map.put("attrd","2");
        SkuItemSaleAttrVo skuItemSaleAttrVo = BeanMapUtils.mapToBean(map, SkuItemSaleAttrVo.class);
        System.out.println(skuItemSaleAttrVo);
    }
@Test
    void test1(){
    List list=new ArrayList<>();
    for (int i = 0; i < 5; i++) {
        ProductImg orderItemEntity = new ProductImg();
        list.add(orderItemEntity);
    }
    productImgMapper.saveAll(list);
}
}
