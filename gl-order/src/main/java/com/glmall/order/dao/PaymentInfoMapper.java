package com.glmall.order.dao;

import com.glmall.order.bean.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInfoMapper extends JpaRepository<PaymentInfo,String> {
}
