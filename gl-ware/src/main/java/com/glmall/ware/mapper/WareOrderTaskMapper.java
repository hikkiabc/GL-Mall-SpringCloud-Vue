package com.glmall.ware.mapper;

import com.glmall.ware.beans.WareOrderTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WareOrderTaskMapper extends JpaRepository<WareOrderTask,String> {
    WareOrderTask findByOrderSn(String sn);
}
