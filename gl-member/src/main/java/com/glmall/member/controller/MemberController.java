package com.glmall.member.controller;

import com.glmall.member.beans.Member;
import com.glmall.member.beans.MemberLevel;
import com.glmall.member.beans.MemberReceiveAddress;
import com.glmall.member.feign.CouponFeign;
import com.glmall.member.mapper.MemberMapper;
import com.glmall.member.service.MemberService;
import com.glmall.utils.AuthConsts;
import com.glmall.utils.CodeEnum;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    CouponFeign couponFeign;
    @Autowired
    MemberService memberService;

    @GetMapping("/memberlevel/{memberLvId}")
    public R getAllMemberLevel(@RequestParam Map map, @PathVariable String memberLvId) {
        Page<MemberLevel> memberLevelPage = memberService.getAllMemberLevel(map, memberLvId);
        return R.ok().put("data", memberLevelPage);
    }

    @PostMapping("memberlevel")
    public R saveMemberLevel(@RequestBody MemberLevel memberLevel) {
        MemberLevel memberLevel1 = memberService.saveMemberLevel(memberLevel);
        return R.ok().put("data", memberLevel);
    }

    @PostMapping("/register")
    public R register(@RequestBody Member member) {
        try {
            Member member1 = memberService.register(member);
            return R.ok().put("data", member1);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(CodeEnum.UNKNOWN_EXCEPTION.getCode(), e.getMessage());
        }
    }

    @PostMapping("/login")
    public R login(@RequestBody Member member, HttpSession httpSession){
      Member member1=  memberService.login(member);
      httpSession.setAttribute("loginUser",member1);
      return  R.ok().put("data",member1);
    }

    @PostMapping("/oauth-login")
    public R oauthLogin(@RequestBody Member member) throws Exception {
      Member member1=  memberService.oauthLogin(member);
      return R.ok().put("data",member1);
    }

    @GetMapping("/memberAddress")
    public R getMemberAddressByMemberId(String id){
       List<MemberReceiveAddress> memberReceiveAddresses= memberService.getMemberAddressByMemberId(id);

       return R.ok().put("data",memberReceiveAddresses);
    }

    @GetMapping("/memberAddressById")
    public R getMemberAddressById(String id){
        MemberReceiveAddress memberReceiveAddresses= memberService.getMemberAddressById(id);

        return R.ok().put("data",memberReceiveAddresses);
    }
}
