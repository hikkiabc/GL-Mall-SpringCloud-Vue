package com.glmall.order.service;

import com.glmall.order.bean.OrderConfirmVo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface OrderConfirmService {
    OrderConfirmVo confirmOrder() throws IOException, ExecutionException, InterruptedException;
}
