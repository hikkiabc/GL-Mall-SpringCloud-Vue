package com.glmall.coupon.mapper;

import com.glmall.coupon.beans.SecKillSkuRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SecKillSkuRelationMapper extends JpaRepository<SecKillSkuRelation,String>, JpaSpecificationExecutor<SecKillSkuRelation> {
    @Modifying
    @Query(value="delete from sec_kill_sku_relation where id in ?1",nativeQuery=true)
    Integer deleteBatchByIds_1(List<String> ids);
@Query(value="select * from sec_kill_sku_relation where promotion_session_id =?1",nativeQuery=true)
  List<SecKillSkuRelation>   findByPromotionSessionId(String id);

}
