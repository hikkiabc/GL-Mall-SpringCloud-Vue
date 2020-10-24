package com.glmall.ware.controller;

import com.glmall.utils.R;
import com.glmall.ware.beans.PurchaseEntity;
import com.glmall.ware.beans.WareInfo;
import com.glmall.ware.service.PurchaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ware/purchase")
public class PurchaseController {

    @Autowired
    PurchaseEntityService purchaseEntityService;
    @GetMapping("{id}")
    public R getAllPurchase(@RequestParam Map map, @PathVariable String id){
       Page<PurchaseEntity> purchaseEntityPage= purchaseEntityService.getAllPurchase(map,id);
       return R.ok().put("data",purchaseEntityPage);
    }
    @PostMapping("")
    public R savePurchaseEntity(@RequestBody PurchaseEntity purchaseEntity){
        PurchaseEntity purchaseEntity1= purchaseEntityService.savePurchaseEntity(purchaseEntity);
        return R.ok().put("data",purchaseEntity1);
    }
    @PostMapping("/merge")
    public R mergePurchaseDetail(@RequestBody Map<String,Object> map){
       Map<String,Object> map1= purchaseEntityService.mergePurchaseDetail(map);
       return R.ok().put("data",map1);
    }
}
