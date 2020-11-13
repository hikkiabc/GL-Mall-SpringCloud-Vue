package com.glmall.member.mapper;

import com.glmall.member.beans.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MemberMapper extends JpaRepository<Member,String>, JpaSpecificationExecutor<Member> {
    @Query(value="select count(*) from member where user_name=?1",nativeQuery=true)
    Long findCountByUsername(String userName);

    @Query(value="select count(*) from member where phone=?1",nativeQuery=true)
    Long findCountByPhone(String phone);

    Member findByUid(String uid);
}
