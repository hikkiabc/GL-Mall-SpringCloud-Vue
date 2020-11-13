package com.glmall.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glmall.cart.vo.CartItemVo;
import com.glmall.cart.vo.CartVo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItemVo addToCart(String skuId, Integer num) throws IOException, ExecutionException, InterruptedException;

    CartItemVo findCartItemBySkuId(String skuId) throws JsonProcessingException;

    CartVo getCart() throws InterruptedException, ExecutionException, JsonProcessingException;

    Boolean clearCart(String tempUserKey);

    void checkItem(String skuId, Integer checked) throws JsonProcessingException;

    void changeCartItemCount(String skuId, Integer num) throws JsonProcessingException;

    void deleteCartItem(String skuId);

    List<CartItemVo> getCheckedCartItemsByUserId();
}
