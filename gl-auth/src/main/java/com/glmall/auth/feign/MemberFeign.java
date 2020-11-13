package com.glmall.auth.feign;

import com.glmall.auth.VO.LoginVo;
import com.glmall.auth.VO.RegisterVo;
import com.glmall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("glmall-member")
public interface MemberFeign {
    @PostMapping("/member/register")
    public R register(@RequestBody RegisterVo registerVo);

    @PostMapping("/member/login")
    public R login(@RequestBody LoginVo loginVo) ;

    @PostMapping("/member/oauth-login")
    public R oauthLogin(@RequestBody Map member) throws Exception;
}
