package com.glmall.utils;

public class Const {
    public enum AttributeEnum{

        BASE_ATTRIBUTE("1","base"),
        SALE_ATTRIBUTE("0","sale");
        private String code;
        private String msg;

        AttributeEnum(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
