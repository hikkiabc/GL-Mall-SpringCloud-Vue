package com.glmall.auth.controller;

import com.glmall.auth.VO.LoginVo;
import com.glmall.auth.VO.RegisterVo;
import com.glmall.auth.feign.MemberFeign;
import com.glmall.auth.feign.SmsFeign;
import com.glmall.utils.AuthConsts;
import com.glmall.utils.CodeEnum;
import com.glmall.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    SmsFeign smsFeign;
    @Autowired
    MemberFeign memberFeign;

    @ResponseBody
    @GetMapping("/sms/{phone}")
    public R sendSms(@PathVariable String phone) {
        String value = redisTemplate.opsForValue().get(AuthConsts.redisSmsPrefix + "_" + phone);
        if (!StringUtils.isBlank(value)) {
            Long savedTime = Long.parseLong(value.split("_")[1]);
            if (System.currentTimeMillis() - savedTime < 60 * 1000) {
                return R.error(CodeEnum.SMS_LIMIT_EXCEPTION.getCode(), CodeEnum.SMS_LIMIT_EXCEPTION.getMsg());
            }
        }

        String code = UUID.randomUUID().toString().substring(0, 5);
        String redisPhoneKey = AuthConsts.redisSmsPrefix + "_" + phone;
        R send = smsFeign.send(phone, code);
        redisTemplate.opsForValue().set(redisPhoneKey,
                code + "_" + System.currentTimeMillis(), 2, TimeUnit.MINUTES);

        return send;
    }

    @PostMapping("/register")
    public String register(@Valid RegisterVo registerVo, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errMap = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
                    FieldError::getDefaultMessage));

            redirectAttributes.addFlashAttribute("errors", errMap);
            return "redirect:http://auth.glmall.com/reg.html";
        }

        String redisCode = redisTemplate.opsForValue().get(AuthConsts.redisSmsPrefix + "_" + registerVo.getPhone()).split("_")[0];
        if (redisCode != null && registerVo.getCode().equals(redisCode)) {
            R register = memberFeign.register(registerVo);
            if ((Integer) register.get("code") != 0) {
                Map errMap = new HashMap();
                errMap.put("msg", register.get("msg"));
                redirectAttributes.addFlashAttribute("errors", errMap);
                return "redirect:http://auth.glmall.com/reg.html";
            } else {
                redisTemplate.delete(AuthConsts.redisSmsPrefix + "_" + registerVo.getPhone());
                redirectAttributes.addFlashAttribute("member", register);
                return "redirect:http://auth.glmall.com/login.html";
            }
        } else {
            Map errMap = new HashMap();
            errMap.put("code", "code wrong");
            redirectAttributes.addFlashAttribute("errors", errMap);
            return "redirect:http://auth.glmall.com/reg.html";
        }
    }

    @GetMapping("/login.html")
    public String toLogin(HttpSession session) {
        if (session.getAttribute(com.glmall.utils.AuthConsts.LOGIN_USER) != null) {
            return "redirect:http://glmall.com";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(LoginVo loginVo,HttpSession session, RedirectAttributes redirectAttributes) {
        R login = memberFeign.login(loginVo);
        if (login.get("data") != null) {
            session.setAttribute(AuthConsts.LOGIN_USER,login.get("data"));
            return "redirect:http://glmall.com";
        } else {
            Map errMap = new HashMap();
            errMap.put("msg", "Login Fail");
            redirectAttributes.addFlashAttribute("errors", errMap);
            return "redirect:http://auth.glmall.com/login.html";
        }
    }
}
