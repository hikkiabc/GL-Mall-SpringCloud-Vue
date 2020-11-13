package com.glmall.member.mapper;

import com.glmall.member.beans.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MemberLevelMapper extends JpaRepository<MemberLevel,String>, JpaSpecificationExecutor<MemberLevel> {

    @Query(value="select * from member_level where default_level=1",nativeQuery=true)
    MemberLevel findByDefaultLevel(String defaultLevel);
}
