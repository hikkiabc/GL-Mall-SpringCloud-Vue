package com.glmall.thirdparty.controller;

import com.aliyuncs.exceptions.ClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.glmall.thirdparty.config.SendSms;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {
    @Autowired
    SendSms sendSms;

    @GetMapping("/sms")
    public R send(String phone,String code) throws ClientException, JsonProcessingException {
    sendSms.send(phone,code);
    return R.ok().put("data",code);
    }
}
