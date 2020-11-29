package com.glmall.glproduct.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glmall.glproduct.beans.ProductCategory;
import com.glmall.glproduct.beans.vo.SkuItemInfoVo;
import com.glmall.glproduct.service.CategoryService;
import com.glmall.glproduct.service.SkuService;
import com.glmall.utils.R;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    SkuService skuService;

    @RequestMapping({"/", "/index.html"})
    public String toIndex(Model model) {
        List<ProductCategory> productCategories = categoryService.findLvl1Category();
        model.addAttribute("lv1Categories", productCategories);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, Object> getCategoryMap() throws JsonProcessingException, InterruptedException {
        Map<String, Object> map = categoryService.getCategoryMap();
        return map;
    }

    @GetMapping("/{productCombId}.html")
    public String toItem(@PathVariable String productCombId,Model model) throws ExecutionException, InterruptedException, IOException {
       SkuItemInfoVo skuItemInfo= skuService.findSkuItemInfo(productCombId);
        System.out.println(skuItemInfo);
       model.addAttribute("item",skuItemInfo);
        return "item";
    }


}
