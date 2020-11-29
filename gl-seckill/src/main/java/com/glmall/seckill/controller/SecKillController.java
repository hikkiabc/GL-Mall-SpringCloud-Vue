package com.glmall.seckill.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glmall.seckill.service.SecKillServiceForGlSeckKill;
import com.glmall.seckill.vo.SecKillSkuRelationVo;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SecKillController {
    @Autowired
    SecKillServiceForGlSeckKill secKillServiceForGlSeckKill;

    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSecKillSkus() {
        List<SecKillSkuRelationVo> secKillSkuRelationVoList = secKillServiceForGlSeckKill.getCurrentSecKillSkus();
        return R.ok().setData(secKillSkuRelationVoList);
    }

    @ResponseBody
    @GetMapping("/getSkuSecKillInfo")
    public R getSkuSecKillInfo(String skuId) throws JsonProcessingException {
        SecKillSkuRelationVo secKillSkuRelationVo = secKillServiceForGlSeckKill.getSkuSecKillInfo(skuId);
        return R.ok().setData(secKillSkuRelationVo);
    }

    @GetMapping("/kill")
    public String secKill(String key, Integer num, String killId, Model model) throws JsonProcessingException {
        String orderSn = secKillServiceForGlSeckKill.secKill(killId, key, num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }

}
