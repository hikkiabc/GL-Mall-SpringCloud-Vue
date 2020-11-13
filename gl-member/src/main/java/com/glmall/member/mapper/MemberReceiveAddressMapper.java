package com.glmall.member.mapper;

import com.glmall.member.beans.MemberReceiveAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberReceiveAddressMapper extends JpaRepository<MemberReceiveAddress,String> {
    List<MemberReceiveAddress> getByUserId(String id);
}
