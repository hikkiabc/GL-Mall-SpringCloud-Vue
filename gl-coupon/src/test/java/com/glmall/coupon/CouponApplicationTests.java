package com.glmall.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class CouponApplicationTests {

    @Test
    void contextLoads() {

                List<String> listOne = new ArrayList<String>();
                listOne.add("333");
                listOne.add("666");
                listOne.add("999");

                List<String> listTwo = new ArrayList<String>();
                listTwo.add("A");
                listTwo.add("B");
                listTwo.add("C");

                //addAll  添加另一集合里面的元素
                //结果[333, 666, 999, A, B, C]
                listOne.addAll(listTwo);
                System.out.println(listOne);
    }

}
