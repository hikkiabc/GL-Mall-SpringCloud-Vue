package com.glmall.member.mapper;

import com.glmall.member.beans.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MemberLevelMapper extends JpaRepository<MemberLevel,String>, JpaSpecificationExecutor<MemberLevel> {
}
