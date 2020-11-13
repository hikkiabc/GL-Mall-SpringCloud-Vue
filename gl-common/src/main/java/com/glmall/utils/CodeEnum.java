package com.glmall.utils;

public enum CodeEnum {
    UNKNOWN_EXCEPTION(10000,"Unknown error"),
    VALID_EXCEPTION(10001,"Valid error"),
    ES_EXCEPTION(10002,"Es error"),
    SMS_LIMIT_EXCEPTION(10003,"Too quickly");
    private int code;
    private String msg;
    CodeEnum(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
