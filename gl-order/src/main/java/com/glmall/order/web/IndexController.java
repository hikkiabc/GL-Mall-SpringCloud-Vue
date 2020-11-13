package com.glmall.order.web;

import com.glmall.order.bean.OrderConfirmVo;
import com.glmall.order.service.OrderConfirmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
public class IndexController {

    @Autowired
    OrderConfirmService orderConfirmService;

    @GetMapping("/toTrade")
    public String toPay(Model model) throws IOException, ExecutionException, InterruptedException {
       OrderConfirmVo orderConfirmVo= orderConfirmService.confirmOrder();
       model.addAttribute("orderConfirmData",orderConfirmVo);
        return "confirm";
    }
}
