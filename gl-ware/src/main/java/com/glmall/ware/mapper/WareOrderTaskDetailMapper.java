package com.glmall.ware.mapper;

import com.glmall.ware.beans.WareOrderTaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface WareOrderTaskDetailMapper extends JpaRepository<WareOrderTaskDetail,String>, JpaSpecificationExecutor<WareOrderTaskDetail> {
    @Query(value="select * from ware_order_task_detail where id =?1",nativeQuery=true)
    WareOrderTaskDetail findById_1(String id);
}
