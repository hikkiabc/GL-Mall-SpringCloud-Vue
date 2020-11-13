package com.glmall.order.bean;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ToString
public class OrderConfirmVo {
    private List<MemberReceiveAddressVo> address;
    private List<CartItemVo> items;
    private Integer points;
    private BigDecimal totalPrice;
    private BigDecimal payPrice;
    private String orderToken;
    private Map<String,Boolean> stocks;

    public Map<String, Boolean> getStocks() {
        return stocks;
    }

    public void setStocks(Map<String, Boolean> stocks) {
        this.stocks = stocks;
    }

    public List<MemberReceiveAddressVo> getAddress() {
        return address;
    }

    public void setAddress(List<MemberReceiveAddressVo> address) {
        this.address = address;
    }

    public List<CartItemVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal sum=new BigDecimal("0");
        for (CartItemVo item : this.items) {
            sum=sum.add(item.getPrice().multiply(new BigDecimal(item.getCount().toString()) ));
        }
        return sum;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getPayPrice() {
        return getTotalPrice();
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }
}
