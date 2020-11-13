package com.glmall.cart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glmall.cart.interceptor.CartInterceptor;
import com.glmall.cart.service.CartService;
import com.glmall.cart.to.UserOrTempUserTo;
import com.glmall.cart.vo.CartItemVo;
import com.glmall.cart.vo.CartVo;
import com.glmall.utils.R;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class IndexController {
    @Autowired
    CartService cartService;

    @GetMapping("/addToCart")
    public String addToCart(String skuId, Integer num, RedirectAttributes redirectAttributes) throws IOException, ExecutionException, InterruptedException {
//        UserOrTempUserTo userOrTempUserTo = CartInterceptor.threadLocal.get();
        CartItemVo cartItemVo = cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", cartItemVo.getSkuId());
        return "redirect:http://cart.glmall.com/toAddToCartSuccess";
    }

    @GetMapping("/toAddToCartSuccess")
    public String toAddToCartSuccess(String skuId, Model model) throws JsonProcessingException {
        CartItemVo cartItemVo = cartService.findCartItemBySkuId(skuId);
        model.addAttribute("item", cartItemVo);
        return "success";
    }

    @GetMapping("/cart.html")
    public String toCart(Model model) throws InterruptedException, ExecutionException, JsonProcessingException {
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    @GetMapping("/checkItem.html")
    public String checkItem(String skuId, Integer check) throws JsonProcessingException {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.glmall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String changeCartItemCount(String skuId, Integer num) throws JsonProcessingException {
        cartService.changeCartItemCount(skuId, num);
        return "redirect:http://cart.glmall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteCartItem(String skuId) {
        cartService.deleteCartItem(skuId);
        return "redirect:http://cart.glmall.com/cart.html";
    }

    @ResponseBody
    @GetMapping("/cartCheckedItemByUserId")
    public R getCheckedCartItemsByUserId() {
        List<CartItemVo> cartItemVos = cartService.getCheckedCartItemsByUserId();
        return R.ok().put("data", cartItemVos);
    }
}
