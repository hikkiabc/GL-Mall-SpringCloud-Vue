package com.glmall.thirdparty.config;

import com.aliyun.oss.ClientException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("spring.sms")
@Component
@Data
public class SendSms {
    private String domain;
    private String signName;
    private String templateCode;
    @Value("${spring.cloud.alicloud.access-key}")
    private String accessKeyId;
    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessSecret;

    public void send(String phone,String code) throws com.aliyuncs.exceptions.ClientException, JsonProcessingException {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(domain);
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        Map<String,Object> paramsMap= new HashMap<>();
        paramsMap.put("code",code);
        ObjectMapper objectMapper = new ObjectMapper();
        request.putQueryParameter("TemplateParam", objectMapper.writeValueAsString(paramsMap));

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}