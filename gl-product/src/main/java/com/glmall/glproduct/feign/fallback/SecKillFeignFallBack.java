package com.glmall.glproduct.feign.fallback;

import com.glmall.glproduct.feign.SecKillFeign;
import com.glmall.utils.CodeEnum;
import com.glmall.utils.R;
import org.springframework.stereotype.Component;

@Component
public class SecKillFeignFallBack implements SecKillFeign {
    @Override
    public R getSkuSecKillInfo(String skuId) {
        System.out.println("fff");
        return R.error(CodeEnum.FLOW_LIMIT.getCode(),CodeEnum.FLOW_LIMIT.getMsg());
    }
}
