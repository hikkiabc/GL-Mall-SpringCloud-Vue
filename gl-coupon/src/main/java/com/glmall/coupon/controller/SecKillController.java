package com.glmall.coupon.controller;

import com.glmall.coupon.beans.SecKillSession;
import com.glmall.coupon.beans.SecKillSkuRelation;
import com.glmall.coupon.service.SecKillService;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
public class SecKillController {
    @Autowired
    SecKillService secKillService;

    @GetMapping("/seckillsession/list")
    public R getSecKillSession(@RequestParam Map<String, Object> params) {
       Page<SecKillSession> secKillSessionPage= secKillService.getSecKillSession(params);
        return R.ok().put("data",secKillSessionPage);
    }

    @GetMapping("/seckillskurelation/list")
    public R getSecKillSkuRelation(@RequestParam Map<String,Object> params){
       Page<SecKillSkuRelation> secKillSkuRelationPage= secKillService.getSecKillSkuRelation(params);
       return R.ok().put("data",secKillSkuRelationPage);
    }

    @GetMapping("/seckillsession/info/{id}")
    public R getSecKillSessionById(@PathVariable String id){
        SecKillSession secKillSession = secKillService.getSecKillSessionById(id);
        return R.ok().put("data",secKillSession);
    }

    @GetMapping("/seckillskurelation/info/{id}")
    public R getSecKillSkuRelationById(@PathVariable String id){
        SecKillSkuRelation secKillSkuRelation = secKillService.getSecKillSkuRelationById(id);
        return R.ok().put("data",secKillSkuRelation);
    }

    @PostMapping("/seckillsession")
    public R saveSecKillSession(@RequestBody SecKillSession secKillSession){
        System.out.println(secKillSession);
        SecKillSession secKillSession1= secKillService.save(secKillSession);
        return R.ok().put("data",secKillSession1);
    }

    @PostMapping("/seckillskurelation")
    public R saveSecKillSkuRelation(@RequestBody SecKillSkuRelation secKillSkuRelation){
        SecKillSkuRelation secKillSkuRelation1= secKillService.saveSecKillSkuRelation(secKillSkuRelation);
        return R.ok().put("data",secKillSkuRelation1);
    }

    @DeleteMapping("/seckillsession")
    public R deleteSecKillSession(@RequestBody List<String> ids){
       Integer res= secKillService.deleteSecKillSession(ids);
       return R.ok().put("data",res);
    }

    @DeleteMapping("/seckillskurelation")
    public R deleteSecKillSkuRelation(@RequestBody List<String> ids){
        Integer res= secKillService.deleteSecKillSkuRelation(ids);
        return R.ok().put("data",res);
    }

    @GetMapping("/latest3DaysSecKillProducts")
    public R getLatest3DaysSecKillProducts(){
      List<SecKillSession> secKillSessionList=  secKillService.getLatest3DaysSecKillProducts();
      return R.ok().setData(secKillSessionList);
    }
}
