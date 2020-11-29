package com.glmall.order.service;

import TO.MemberTo;
import TO.ProductCombStockTO;
import TO.SecKillOrderTo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.glmall.order.bean.*;
import com.glmall.order.dao.OrderEntityMapper;
import com.glmall.order.dao.OrderItemEntityMapper;
import com.glmall.order.dao.PaymentInfoMapper;
import com.glmall.order.feign.CartFeign;
import com.glmall.order.feign.MemberFeign;
import com.glmall.order.feign.ProductFeign;
import com.glmall.order.feign.WareFeign;
import com.glmall.order.interceptor.OrderInterceptor;
import com.glmall.order.utils.OrderStatusEnum;
import com.glmall.order.vo.PayAsyncVo;
import com.glmall.order.vo.PayVo;
import com.glmall.utils.R;
import com.glmall.utils.SnowFlakeWorker;
import com.glmall.utils.validate.OrderConsts;
import exception.NoStockException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImp implements OrderService {
    @Autowired
    MemberFeign memberFeign;
    @Autowired
    CartFeign cartFeign;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    WareFeign wareFeign;
    @Autowired
    PaymentInfoMapper paymentInfoMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeign productFeign;
    @Autowired
    OrderEntityMapper orderEntityMapper;
    @Autowired
    OrderItemEntityMapper orderItemEntityMapper;
    @Autowired
    EntityManager entityManager;
    @Autowired
    RabbitTemplate rabbitTemplate;

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
            R stockList = wareFeign.getProductCombHasStockByProductCombIdList(skuIdList);
            try {
                List<ProductCombStockTO> productCombStockTOS = stockList.getData(new TypeReference<>() {
                });
                Map<String, Boolean> hasStockMap = productCombStockTOS.stream().collect(Collectors.toMap(ProductCombStockTO::getProductCombId,
                        ProductCombStockTO::getHasStock));
                orderConfirmVo.setStocks(hasStockMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        String s = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVo.setOrderToken(s);
        redisTemplate.opsForValue().set(OrderConsts.USER_ORDER_TOKEN_PRFIX + memberTo.getId(), s, 30, TimeUnit.MINUTES);
        orderConfirmVo.setPoints(memberTo.getPoints());
        CompletableFuture.allOf(addressT, cartItemT).get();
        return orderConfirmVo;
    }

    @Override
    public OrderSubmitResponseVo submitOrder(OrderSubmitVo orderSubmitVo) throws IOException {
        MemberTo memberTo = OrderInterceptor.threadLocal.get();
        SnowFlakeWorker snowFlakeWorker = new SnowFlakeWorker();
        OrderSubmitResponseVo orderSubmitResponseVo = new OrderSubmitResponseVo();
        String delete_redisToken_script
                = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(delete_redisToken_script, Long.class),
                Arrays.asList(OrderConsts.USER_ORDER_TOKEN_PRFIX + memberTo.getId()),
                orderSubmitVo.getOrderToken());
        if (execute == 0) {
            orderSubmitResponseVo.setCode(1);
        } else {
            R deliveryFareByAddressId = wareFeign.getDeliveryFareByAddressId(orderSubmitVo.getAddrId());
            FareVo fareVo = deliveryFareByAddressId.getData(new TypeReference<>() {
            });
            String sn = snowFlakeWorker.nextId() + "";
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setFreightPrice(fareVo.getFare());
            orderEntity.setMemberId(fareVo.getMemberAddressVo().getUserId());
            orderEntity.setMemberUsername(fareVo.getMemberAddressVo().getName());
            orderEntity.setOrderSn(sn);
            orderEntity.setReceiverName(memberTo.getName());
            orderEntity.setPayType(orderSubmitVo.getPayType());
            List<OrderItemEntity> orderItemEntityList = getOrderItemEntityList(sn);
            BigDecimal totalPrice = new BigDecimal("0");
            BigDecimal reducedPrice = new BigDecimal("0");
            for (OrderItemEntity orderItemEntity : orderItemEntityList) {
                totalPrice = totalPrice.add(orderItemEntity.getSkuPrice());
                reducedPrice = reducedPrice.add(new BigDecimal(orderItemEntity.getPromotionAmount()));
            }
            orderEntity.setTotalPrice(totalPrice);
            orderEntity.setTotalReducedPrice(reducedPrice);
            orderEntity.setPayPrice(totalPrice.add(orderEntity.getFreightPrice()));
            if (Math.abs(orderSubmitVo.getPayPrice().subtract(orderEntity.getPayPrice()).doubleValue()) < 0.01) {
                OrderEntity saveOrderEntity = orderEntityMapper.save(orderEntity);
                List<OrderItemEntity> saveOrderItemEntityList1 = orderItemEntityMapper.saveAll(orderItemEntityList);
                LockOrderStockVo lockOrderStockVo = new LockOrderStockVo();
                lockOrderStockVo.setOrderSn(sn);
                lockOrderStockVo.setOrderItemList(saveOrderItemEntityList1);
                R r = wareFeign.lockOrderItemStock(lockOrderStockVo);
                if ((Integer) r.get("code") == 0) {
                    orderSubmitResponseVo.setCode(0);
                    orderSubmitResponseVo.setOrder(saveOrderEntity);
                    rabbitTemplate.convertAndSend("order-event-exchange",
                            "order.created.order",orderEntity);
                } else {
                    throw new NoStockException("skuId na");
                }
            } else {
                orderSubmitResponseVo.setCode(2);
            }
        }
        return orderSubmitResponseVo;
    }

    @Override
    public OrderEntity getOrderBySn(String sn) {
        OrderEntity byOrderSn = orderEntityMapper.findByOrderSn(sn);
        return byOrderSn;
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        OrderEntity orderEntity1 = orderEntityMapper.findById_1(orderEntity.getId());
        if (orderEntity1!=null&&orderEntity1.getStatus().equals(OrderStatusEnum.NEW.getCode())){
            orderEntityMapper.closeOrder(orderEntity);
            //try catch, save msg status to a new db table to monitoring msg status,
            // update status in callback fn (in rabbit config)
            rabbitTemplate.convertAndSend("order-event-exchange","order.close.order",orderEntity);
        }
    }

    @Override
    public PayVo payOrder(String orderSn) throws IOException {
        OrderEntity orderBySn = getOrderBySn(orderSn);
        List<OrderItemEntity> orderItemEntityList = getOrderItemEntityList(orderSn);
        String skuAttrsValues = orderItemEntityList.get(0).getSkuAttrsValues();
        PayVo payVo = new PayVo();
        payVo.setBody(skuAttrsValues);
        payVo.setSubject(orderItemEntityList.get(0).getSkuName());
        payVo.setOut_trade_no(orderSn);
        payVo.setTotal_amount(orderBySn.getPayPrice()
                .setScale(2, RoundingMode.UP).toString());
        return payVo;
    }

    @Override
    public Page<OrderEntity> getOrderWithItem(String id, Map<String, Object> params) {
        PageRequest of = PageRequest.of(Integer.valueOf(params.get("pageNum").toString()) - 1,
                Integer.valueOf(params.get("pageSize").toString()));
        Page<OrderEntity> byMemberId = orderEntityMapper.findByMemberId(id, of);
        byMemberId.getContent().stream().map(order -> {
            List<OrderItemEntity> byOrderSn = orderItemEntityMapper.findByOrderSn(order.getOrderSn());
            order.setItemEntities(byOrderSn);
            return order;
        }).collect(Collectors.toList());

        return byMemberId;
    }

    @Override
    public String handlePayAsyncNotify(PayAsyncVo vo) {
        PaymentInfo infoEntity = new PaymentInfo();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());
        infoEntity.setSubject(vo.getSubject());
        infoEntity.setTotalAmount(new BigDecimal(vo.getTotal_amount()));
        infoEntity.setCreateTime(vo.getGmt_create());
        paymentInfoMapper.save(infoEntity);

        if(vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")){
            String orderSn = vo.getOut_trade_no();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<OrderEntity> update = cb.createCriteriaUpdate(OrderEntity.class);
            Root<OrderEntity> root = update.from(OrderEntity.class);
            Predicate predicateOrderSn = cb.equal(root.get("orderSn"), orderSn);
            update.set(root.get("status"),OrderStatusEnum.PAID.getCode());
            update.where(predicateOrderSn);
            int executeUpdate = entityManager.createQuery(update).executeUpdate();

            // method2:
//            orderEntityMapper.updateOrderStatus(orderSn, OrderStatusEnum.PAID.getCode());
            return "success";
        }
        return "payment fail";
    }

    @Override
    public void createSecKillOrder(SecKillOrderTo secKillOrderTo) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(secKillOrderTo.getOrderSn());
        orderEntity.setStatus(OrderStatusEnum.NEW.getCode());
        orderEntity.setMemberId(secKillOrderTo.getUserId());
        BigDecimal payPrice = secKillOrderTo.getSeckillPrice().
                multiply(new BigDecimal(secKillOrderTo.getNum() + ""));
        orderEntity.setPayPrice(payPrice);
        orderEntityMapper.save(orderEntity);
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setSkuId(secKillOrderTo.getSkuId());
        orderItemEntity.setOrderSn(secKillOrderTo.getOrderSn());
        orderItemEntity.setSkuQuantity(secKillOrderTo.getNum());
        orderItemEntity.setTotalPrice(payPrice);
        orderItemEntityMapper.save(orderItemEntity);
    }

    private List<OrderItemEntity> getOrderItemEntityList(String orderSn) throws IOException {
        R checkedCartItemsByUserId = cartFeign.getCheckedCartItemsByUserId();
        List<CartItemVo> cartItemVoList = checkedCartItemsByUserId.getData(new TypeReference<>() {
        });
        if (cartItemVoList != null && cartItemVoList.size() > 0) {
            List<OrderItemEntity> orderItemEntityList = cartItemVoList.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = new OrderItemEntity();
                orderItemEntity.setSkuQuantity(cartItem.getCount());
                orderItemEntity.setGiftGrowth(cartItem.getPrice().intValue());
                orderItemEntity.setOrderSn(orderSn);
                orderItemEntity.setSkuId(cartItem.getSkuId());
                orderItemEntity.setSkuAttrsValues(
                        StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";"));
                orderItemEntity.setSkuName(cartItem.getTitle());
                orderItemEntity.setSkuPic(cartItem.getImage());
                orderItemEntity.setSkuPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())));
                R productBySkuId = productFeign.getProductBySkuId(cartItem.getSkuId());
                try {
                    ProductVo productVo = productBySkuId.getData(new TypeReference<>() {
                    });
                    orderItemEntity.setSpuId(productVo.getId());
                    orderItemEntity.setSpuBrand(productVo.getBrandName());
                    orderItemEntity.setSpuName(productVo.getName());
                    orderItemEntity.setCategory_id(productVo.getCategoryId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return orderItemEntity;
            }).collect(Collectors.toList());
            return orderItemEntityList;
        }
        return null;
    }
}

