package com.glmall.auth.feign;

import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gl-thirdparty")
public interface SmsFeign {

    @GetMapping("/sms")
    public R send(@RequestParam("phone") String phone,@RequestParam("code") String code);
}
