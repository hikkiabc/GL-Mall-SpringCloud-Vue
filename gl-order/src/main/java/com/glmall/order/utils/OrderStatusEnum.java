package com.glmall.order.utils;

public enum  OrderStatusEnum {
    NEW(0,"new"),
    PAID(2,"paid"),
    SEND(3,"SEND"),
    RECEIVED(5,"RECEIVED"),
    CANCELED(4,"CANCELED"),
    SERVING(6,"SERVING"),
    SERVED(7,"SERVED");
    private Integer code;
    private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
