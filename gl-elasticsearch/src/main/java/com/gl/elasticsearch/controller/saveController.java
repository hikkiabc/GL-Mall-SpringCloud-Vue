package com.gl.elasticsearch.controller;

import ES.EsProductComb;
import com.gl.elasticsearch.service.SaveService;
import com.glmall.utils.CodeEnum;
import com.glmall.utils.Const;
import com.glmall.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/es/save")
public class saveController {
    @Autowired
    SaveService saveService;

    @PostMapping("/productComb")
    public R saveProductComb(@RequestBody List<EsProductComb> esProductCombs){
        Boolean res=false;
        try {
             res=saveService.saveProductComb(esProductCombs);
        } catch (Exception e) {
            log.error("es save err",e);
            e.printStackTrace();
            return R.error(CodeEnum.ES_EXCEPTION.getCode(),CodeEnum.ES_EXCEPTION.getMsg());
        }
        if (!res){
            return R.ok();
        }else {
            return R.error(CodeEnum.ES_EXCEPTION.getCode(),CodeEnum.ES_EXCEPTION.getMsg());
        }
    }
}
