package com.glmall.ware.service;

import TO.WareOrderTaskTo;
import com.glmall.ware.vo.LockOrderStockVo;
import com.glmall.ware.vo.OrderVo;

import java.io.IOException;

public interface StockService {
    Boolean lockOrderItemStock(LockOrderStockVo lockOrderStockVos);

    void unLockStock(WareOrderTaskTo wareOrderTaskTo) throws IOException;

    void unLockStock(OrderVo orderVo);
}
