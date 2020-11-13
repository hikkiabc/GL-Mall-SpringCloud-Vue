package com.glmall.cart.service;

import TO.ProductCombTo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glmall.cart.feign.ProductFeign;
import com.glmall.cart.interceptor.CartInterceptor;
import com.glmall.cart.to.UserOrTempUserTo;
import com.glmall.cart.vo.CartItemVo;
import com.glmall.cart.vo.CartVo;
import com.glmall.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImp implements CartService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProductFeign productFeign;
    @Autowired
    ThreadPoolExecutor executor;

    private final String REDIS_CART_PREFIX = "glmall:cart:";

    @Override
    public CartItemVo addToCart(String skuId, Integer num) throws ExecutionException, InterruptedException, JsonProcessingException {
        BoundHashOperations<String, Object, Object> cartKeyBoundHashOps = getCartKeyBoundHashOps();
        String redisCartItem = (String) cartKeyBoundHashOps.get(skuId);
        ObjectMapper objectMapper = new ObjectMapper();
        if (StringUtils.isBlank(redisCartItem)) {
            CartItemVo cartItemVo = new CartItemVo();
            CompletableFuture<Void> getProductCombTo = CompletableFuture.runAsync(() -> {
                R r = productFeign.getProductCombByProductCombId(skuId);
                try {
                    ProductCombTo productCombTo = r.getData(new TypeReference<>() {
                    });
                    cartItemVo.setCount(num);
                    cartItemVo.setImage(productCombTo.getDefaultImg());
                    cartItemVo.setSkuId(productCombTo.getId());
                    cartItemVo.setPrice(productCombTo.getPrice());
                    cartItemVo.setTitle(productCombTo.getTitle());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, executor);
            CompletableFuture<Void> getSaleAttrs = CompletableFuture.runAsync(() -> {
                R skuSaleAttrsBySkuId = productFeign.getSkuSaleAttrsBySkuId(skuId);
                List<String> saleAttrs = (List<String>) skuSaleAttrsBySkuId.get("data");
                cartItemVo.setSkuAttr(saleAttrs);
            }, executor);
            CompletableFuture.allOf(getSaleAttrs, getProductCombTo).get();
            String s = objectMapper.writeValueAsString(cartItemVo);
            cartKeyBoundHashOps.put(skuId, s);
            return cartItemVo;
        } else {
            CartItemVo cartItemVo;
            CartItemVo cartItemVo1 = objectMapper.readValue(redisCartItem, CartItemVo.class);
            cartItemVo1.setCount(cartItemVo1.getCount() + num);
            cartItemVo = cartItemVo1;
            String s = objectMapper.writeValueAsString(cartItemVo);
            cartKeyBoundHashOps.put(skuId, s);
            return cartItemVo;
        }
    }

    @Override
    public CartItemVo findCartItemBySkuId(String skuId) throws JsonProcessingException {
        BoundHashOperations<String, Object, Object> cartKeyBoundHashOps = getCartKeyBoundHashOps();
        String s = (String) cartKeyBoundHashOps.get(skuId);
        ObjectMapper objectMapper = new ObjectMapper();
        CartItemVo cartItemVo = objectMapper.readValue(s, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws InterruptedException, ExecutionException, JsonProcessingException {
        UserOrTempUserTo userOrTempUserTo = CartInterceptor.threadLocal.get();
        CartVo cartVo = new CartVo();
        if (!StringUtils.isBlank(userOrTempUserTo.getUserId())) {
            List<CartItemVo> cartItemByCartKey = getCartItemByCartKey(userOrTempUserTo.getTempUserKey());
            if (cartItemByCartKey!=null){
                for (CartItemVo cartItemVo : cartItemByCartKey) {
                    addToCart(cartItemVo.getSkuId(),cartItemVo.getCount());
                }
                clearCart(userOrTempUserTo.getTempUserKey());
            }
            List<CartItemVo> cartItemByCartKey1 = getCartItemByCartKey(userOrTempUserTo.getUserId());
            cartVo.setItems(cartItemByCartKey1);
        }else{
            List<CartItemVo> cartItemByCartKey = getCartItemByCartKey(userOrTempUserTo.getTempUserKey());
            cartVo.setItems(cartItemByCartKey);
        }
        return cartVo;
    }

    @Override
    public Boolean clearCart(String tempUserKey) {
        Boolean delete = stringRedisTemplate.delete(REDIS_CART_PREFIX+tempUserKey);
        return delete;
    }

    @Override
    public void checkItem(String skuId, Integer checked) throws JsonProcessingException {
        CartItemVo cartItemBySkuId = findCartItemBySkuId(skuId);
        cartItemBySkuId.setChecked(checked==1);
        BoundHashOperations<String, Object, Object> cartKeyBoundHashOps = getCartKeyBoundHashOps();
        ObjectMapper objectMapper = new ObjectMapper();
        cartKeyBoundHashOps.put(skuId,objectMapper.writeValueAsString(cartItemBySkuId));
    }

    @Override
    public void changeCartItemCount(String skuId, Integer num) throws JsonProcessingException {
        CartItemVo cartItemBySkuId = findCartItemBySkuId(skuId);
        cartItemBySkuId.setCount(num);
        ObjectMapper objectMapper = new ObjectMapper();
        BoundHashOperations<String, Object, Object> cartKeyBoundHashOps = getCartKeyBoundHashOps();
        cartKeyBoundHashOps.put(skuId,objectMapper.writeValueAsString(cartItemBySkuId));
    }

    @Override
    public void deleteCartItem(String skuId) {
        BoundHashOperations<String, Object, Object> cartKeyBoundHashOps = getCartKeyBoundHashOps();
        cartKeyBoundHashOps.delete(skuId);
    }

    @Override
    public List<CartItemVo> getCheckedCartItemsByUserId() {
        UserOrTempUserTo userOrTempUserTo = CartInterceptor.threadLocal.get();
        if (userOrTempUserTo.getUserId()!=null){
            List<CartItemVo> cartItemByCartKey = getCartItemByCartKey( userOrTempUserTo.getUserId());
            List<CartItemVo> collect = cartItemByCartKey.stream().filter(e -> e.getChecked()).map(e -> {
                R productCombByProductCombId = productFeign.getProductCombByProductCombId(e.getSkuId());
                Map<String, Object> map = (Map<String, Object>) productCombByProductCombId.get("data");
                BigDecimal price = new BigDecimal(map.get("price").toString()) ;
                e.setPrice(price);
                return e;
            }).collect(Collectors.toList());
            return collect;

        }
        return null;

    }


    private BoundHashOperations<String, Object, Object> getCartKeyBoundHashOps() {
        UserOrTempUserTo userOrTempUserTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (!StringUtils.isBlank(userOrTempUserTo.getUserId())) {
            cartKey = REDIS_CART_PREFIX + userOrTempUserTo.getUserId();
        } else {
            cartKey = REDIS_CART_PREFIX + userOrTempUserTo.getTempUserKey();
        }
        BoundHashOperations<String, Object, Object> boundHashOperations = stringRedisTemplate.boundHashOps(cartKey);
        return boundHashOperations;
    }

    private List<CartItemVo> getCartItemByCartKey(String key){
        ObjectMapper objectMapper = new ObjectMapper();
        BoundHashOperations<String, Object, Object> boundHashOperations = stringRedisTemplate.boundHashOps(REDIS_CART_PREFIX+ key);
        if (boundHashOperations.values()!=null){
            List<CartItemVo> collect = boundHashOperations.values().stream().map(e -> {
                try {
                    return objectMapper.readValue((String) e, CartItemVo.class);
                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }
}
