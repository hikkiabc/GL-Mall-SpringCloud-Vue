package com.glmall.member.web;

import com.glmall.member.feign.OrderFeign;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    OrderFeign orderFeign;

    @GetMapping("/memberOrder.html")
    public String toMemberOrderList(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                    @RequestParam(required = false,defaultValue = "2")Integer pageSize,
                                    Model model) {
        Map<String,Object>map=new HashMap<>();
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        R orderWithItem = orderFeign.getOrderWithItem(map);
        model.addAttribute("orders",orderWithItem);
        return "orderList";
    }

}
