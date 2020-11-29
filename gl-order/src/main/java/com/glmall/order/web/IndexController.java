package com.glmall.order.web;

import TO.MemberTo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.glmall.order.bean.OrderConfirmVo;
import com.glmall.order.bean.OrderEntity;
import com.glmall.order.bean.OrderSubmitResponseVo;
import com.glmall.order.bean.OrderSubmitVo;
import com.glmall.order.config.AlipayTemplate;
import com.glmall.order.interceptor.OrderInterceptor;
import com.glmall.order.service.OrderService;
import com.glmall.order.vo.PayAsyncVo;
import com.glmall.order.vo.PayVo;
import com.glmall.utils.R;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class IndexController {

    @Autowired
    OrderService orderService;
    @Autowired
    AlipayTemplate alipayTemplate;

    @GetMapping("/toTrade")
    public String toPay(Model model) throws IOException, ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model,
                              RedirectAttributes redirectAttributes) throws IOException {
        OrderSubmitResponseVo orderSubmitResponseVo = orderService.submitOrder(orderSubmitVo);
        if (orderSubmitResponseVo.getCode() == 0) {
            model.addAttribute("submitOrderResp", orderSubmitResponseVo);
            return "pay";
        } else {
            redirectAttributes.addFlashAttribute("msg", "submit fail");
            return "redirect:http://order.glmall.com/toTrade";
        }
    }

    @ResponseBody
    @GetMapping("/order/{orderSn}")
    public R getOrderBySn(@PathVariable String orderSn) {
        OrderEntity orderEntity = orderService.getOrderBySn(orderSn);
        return R.ok().put("data", orderEntity);
    }

    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(String orderSn) throws IOException, AlipayApiException {
        PayVo payVo = orderService.payOrder(orderSn);
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }

    @ResponseBody
    @PostMapping("/getOrderWithItem")
    public R getOrderWithItem(@RequestBody Map<String, Object> params) {
        MemberTo memberTo = OrderInterceptor.threadLocal.get();
        Page<OrderEntity> entityPage = orderService.getOrderWithItem(memberTo.getId(), params);
        return R.ok().put("data", entityPage);
    }

    @ResponseBody
    @PostMapping("/paid/memberOrderNotify")
    public String paidNotify(PayAsyncVo payAsyncVo, HttpServletRequest request) throws AlipayApiException {
        Map<String,String> params = new HashMap<>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        if (AlipaySignature.rsaCheckV1(params,alipayTemplate.getAlipay_public_key(),alipayTemplate.getCharset(),
                alipayTemplate.getSign_type())){
            return   orderService.handlePayAsyncNotify(payAsyncVo);
        }
        return "validate fail";
    }
}
