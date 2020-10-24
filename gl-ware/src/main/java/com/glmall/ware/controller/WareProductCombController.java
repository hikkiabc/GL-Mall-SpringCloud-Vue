package com.glmall.ware.controller;

import TO.ProductCombStockTO;
import com.glmall.utils.R;
import com.glmall.ware.beans.ProductCombWare;
import com.glmall.ware.beans.PurchaseDetail;
import com.glmall.ware.beans.WareInfo;
import com.glmall.ware.service.WareProductCombService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ware")
public class WareProductCombController {
    @Autowired
    WareProductCombService wareProductCombService;
    @GetMapping("/product_comb")
    public R getWareProductComb(@RequestParam Map map){
      Page<ProductCombWare> productComboWarePage=  wareProductCombService.getWareProductComb(map);
      return R.ok().put("data",productComboWarePage);
    }
    @PostMapping("/product_comb/stock")
    public R getProductCombHasStockByProductCombIdList(@RequestBody List<String> idList){
       List<ProductCombStockTO> productCombStockTOS= wareProductCombService.getProductCombHasStockByProductCombIdList(idList);
        R ok = R.ok();
        ok.setData(productCombStockTOS);
        return ok;
    }
    @PostMapping("/product_comb")
    public R saveWareProductComb(@RequestBody ProductCombWare productCombWare){
        ProductCombWare productCombWare1 = wareProductCombService.saveWareProductComb(productCombWare);
        return R.ok().setData(productCombWare1);
    }
    @GetMapping( "{id}")
    public R getWareInfo(@RequestParam Map map, @PathVariable String id){
       Page<WareInfo> wareInfoPage= wareProductCombService.getWareInfo(map,id);
       return R.ok().put("data",wareInfoPage);
    }
    @PostMapping("{id}")
    public R saveWareInfo(@RequestBody WareInfo wareInfo){
       WareInfo wareInfo1= wareProductCombService.saveWareInfo(wareInfo);
       return R.ok().put("data",wareInfo1);
    }
    @GetMapping("/purchase-detail/{id}")
    public R getPurchaseDetail(@RequestParam Map map, @PathVariable String id){
       Page<PurchaseDetail> purchaseDetailPage= wareProductCombService.getPurchaseDetail(map,id);
       return R.ok().put("data",purchaseDetailPage);
    }
    @PostMapping("/purchase-detail")
    public R savePurchaseDetail(@RequestBody PurchaseDetail purchaseDetail){
       PurchaseDetail purchaseDetail1= wareProductCombService.savePurchaseDetail(purchaseDetail);
       return R.ok().put("data",purchaseDetail1);
    }
}
