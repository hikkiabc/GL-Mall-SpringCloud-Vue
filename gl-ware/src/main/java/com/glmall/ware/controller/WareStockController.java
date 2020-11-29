package com.glmall.ware.controller;

import com.glmall.utils.CodeEnum;
import com.glmall.utils.R;
import com.glmall.ware.service.StockService;
import com.glmall.ware.vo.LockOrderItemStockResponseVo;
import com.glmall.ware.vo.LockOrderStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ware/stock")
public class WareStockController {

    @Autowired
    StockService stockService;

    @PostMapping("/lockOrderItemStock")
    public R lockOrderItemStock(@RequestBody LockOrderStockVo LockOrderStockVo){
        try {
            Boolean lockRes = stockService.lockOrderItemStock(LockOrderStockVo);
            return R.ok().put("data",lockRes);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(CodeEnum.LOCK_STOCK_FAIL_ERROR.getCode(),CodeEnum.LOCK_STOCK_FAIL_ERROR.getMsg());
        }

    }
}
