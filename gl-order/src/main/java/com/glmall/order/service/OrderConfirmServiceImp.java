package com.glmall.order.service;

import TO.MemberTo;
import TO.ProductCombStockTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.glmall.order.bean.CartItemVo;
import com.glmall.order.bean.MemberReceiveAddressVo;
import com.glmall.order.bean.OrderConfirmVo;
import com.glmall.order.feign.CartFeign;
import com.glmall.order.feign.MemberFeign;
import com.glmall.order.feign.WareFeign;
import com.glmall.order.interceptor.OrderInterceptor;
import com.glmall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderConfirmServiceImp implements OrderConfirmService {
    @Autowired
    MemberFeign memberFeign;
    @Autowired
    CartFeign cartFeign;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    WareFeign wareFeign;

    @Override
    public OrderConfirmVo confirmOrder() throws IOException, ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberTo memberTo = OrderInterceptor.threadLocal.get();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> addressT = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R memberAddressByMemberId = memberFeign.getMemberAddressByMemberId(memberTo.getId());
            List<MemberReceiveAddressVo> memberReceiveAddressVoList = null;
            try {
                memberReceiveAddressVoList = memberAddressByMemberId.getData(new TypeReference<>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            orderConfirmVo.setAddress(memberReceiveAddressVoList);
        });

        CompletableFuture<Void> cartItemT = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R checkedCartItemsByUserId = cartFeign.getCheckedCartItemsByUserId();
            List<CartItemVo> cartItemVoList = null;
            try {
                cartItemVoList = checkedCartItemsByUserId.getData(new TypeReference<>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            orderConfirmVo.setItems(cartItemVoList);
        }).thenRunAsync(() -> {
            List<String> skuIdList = orderConfirmVo.getItems().stream().map(CartItemVo::getSkuId).collect(Collectors.toList());
            R stockList =  wareFeign.getProductCombHasStockByProductCombIdList(skuIdList);
            try {
                List<ProductCombStockTO> productCombStockTOS= stockList.getData(new TypeReference<>() {
                 });
                Map<String, Boolean> hasStockMap  = productCombStockTOS.stream().collect(Collectors.toMap(ProductCombStockTO::getProductCombId,
                        ProductCombStockTO::getHasStock));
                orderConfirmVo.setStocks(hasStockMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        orderConfirmVo.setPoints(memberTo.getPoints());
        CompletableFuture.allOf(addressT, cartItemT).get();
        return orderConfirmVo;
    }
}
