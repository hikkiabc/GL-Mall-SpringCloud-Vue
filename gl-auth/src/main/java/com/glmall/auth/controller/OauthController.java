package com.glmall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glmall.auth.feign.MemberFeign;
import com.glmall.utils.HttpUtils;
import com.glmall.utils.R;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OauthController {
    @Autowired
    MemberFeign memberFeign;

    @GetMapping("/oauth/weibo")
    public String oauthWeiboReceiveCode(String code, HttpSession httpSession) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("client_id", "3200035825");
        map.put("client_secret", "ec2a3e94f78ff10d1b8bc3a6092e6744");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.glmall.com/oauth/weibo");
        map.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token",
                "post", new HashMap<>(), null,map);

        String toString = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        Map map1 = objectMapper.readValue(toString, Map.class);
//        System.out.println(map1);
//        String access_token = (String) map1.get("access_token");
//        String expires_in = (String) map1.get("expires_in");
//        String uid = (String) map1.get("uid");
        R r = memberFeign.oauthLogin(map1);
        Map data = (Map) r.get("data");
        httpSession.setAttribute("loginUser",data);
        if ((Integer) r.get("code")==0){
            return "redirect:http://glmall.com";
        }
        return  "redirect:http://auth.glmall.com/login.html";
    }
}
