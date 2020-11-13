package com.glmall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {
    private List<CartItemVo> items;
    private Integer totalCount;
    private Integer typeCount;
    private BigDecimal totalPrice;
    private BigDecimal reduce=new BigDecimal("0");


    public Integer getTotalCount() {
        Integer i=0;
        for (CartItemVo item : this.items) {
            i+=item.getCount();
        }
        return i;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    public List<CartItemVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    public Integer getTypeCount() {
        int i=0;
        for (CartItemVo item : this.items) {
            i+=1;
        }
        return i;
    }

    public void setTypeCount(Integer typeCount) {
        this.typeCount = typeCount;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (CartItemVo item : this.items) {
            if (item.getChecked()){
                bigDecimal=bigDecimal.add(item.getTotalPrice());
            }
        }
        bigDecimal=bigDecimal.subtract(this.reduce);
        return bigDecimal;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }


}
