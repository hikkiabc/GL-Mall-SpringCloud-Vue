package com.glmall.member.service;

import com.glmall.member.beans.Member;
import com.glmall.member.beans.MemberLevel;
import com.glmall.member.beans.MemberReceiveAddress;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface MemberService {
    Page<MemberLevel> getAllMemberLevel(Map map, String memberLvId);

    MemberLevel saveMemberLevel(MemberLevel memberLevel);

    Member register(Member member);

    Member login(Member member);

    Member oauthLogin(Member member) throws Exception;

    List<MemberReceiveAddress> getMemberAddressByMemberId(String id);

    MemberReceiveAddress getMemberAddressById(String id);
}
