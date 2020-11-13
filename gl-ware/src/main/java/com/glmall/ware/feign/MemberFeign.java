package com.glmall.ware.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("glmall-member")
public interface MemberFeign {

    @GetMapping("/member/memberAddressById")
    public R getMemberAddressById(@RequestParam String id);
}
