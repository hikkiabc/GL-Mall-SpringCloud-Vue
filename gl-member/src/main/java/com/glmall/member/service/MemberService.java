package com.glmall.member.service;

import com.glmall.member.beans.MemberLevel;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface MemberService {
    Page<MemberLevel> getAllMemberLevel(Map map, String memberLvId);

    MemberLevel saveMemberLevel(MemberLevel memberLevel);
}
