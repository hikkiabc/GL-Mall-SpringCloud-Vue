package com.glmall.member.controller;

import com.glmall.member.beans.MemberLevel;
import com.glmall.member.feign.CouponFeign;
import com.glmall.member.service.MemberService;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    CouponFeign couponFeign;
    @Autowired
    MemberService memberService;
    @GetMapping("/memberlevel/{memberLvId}")
    public R getAllMemberLevel(@RequestParam Map map, @PathVariable String memberLvId){
        Page<MemberLevel> memberLevelPage= memberService.getAllMemberLevel(map,memberLvId);
        return R.ok().put("data",memberLevelPage);
    }
    @PostMapping("memberlevel")
    public R saveMemberLevel(@RequestBody MemberLevel memberLevel){
       MemberLevel memberLevel1= memberService.saveMemberLevel(memberLevel);
       return R.ok().put("data",memberLevel);
    }
}
