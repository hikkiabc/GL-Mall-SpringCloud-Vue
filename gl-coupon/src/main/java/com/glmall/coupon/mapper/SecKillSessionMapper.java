package com.glmall.coupon.mapper;

import com.glmall.coupon.beans.SecKillSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SecKillSessionMapper extends JpaRepository<SecKillSession,String>, JpaSpecificationExecutor<SecKillSession> {
    @Modifying
    @Query(value="delete from sec_kill_session where id in ?1",nativeQuery=true)
    Integer deleteBatchByIds_1(List<String> ids);

    @Query(value="select * from sec_kill_session where start_time between ?1 and ?2",nativeQuery=true)
    List<SecKillSession> findLatest3DaysSecKillSession(String first,String three);
}
