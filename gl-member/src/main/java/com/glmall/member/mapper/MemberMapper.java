package com.glmall.member.mapper;

import com.glmall.member.beans.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMapper extends JpaRepository<Member,String> {
}
