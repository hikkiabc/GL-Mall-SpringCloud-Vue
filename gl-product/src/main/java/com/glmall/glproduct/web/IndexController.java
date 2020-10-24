package com.glmall.glproduct.web;

import com.glmall.glproduct.beans.ProductCategory;
import com.glmall.glproduct.service.CategoryService;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @RequestMapping({"/", "/index.html"})
    public String toIndex(Model model) {
        List<ProductCategory> productCategories = categoryService.findLvl1Category();
        model.addAttribute("lv1Categories", productCategories);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, Object> getCategoryMap() {
        Map<String, Object> map = categoryService.getCategoryMap();
        return map;
    }

}
