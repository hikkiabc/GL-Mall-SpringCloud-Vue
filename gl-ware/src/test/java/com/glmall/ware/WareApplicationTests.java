package com.glmall.ware;

import com.glmall.ware.beans.WareOrderTask;
import com.glmall.ware.mapper.WareOrderTaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WareApplicationTests {
@Autowired
    WareOrderTaskMapper wareOrderTaskMapper;
    @Test
    void contextLoads() {

    }

}
