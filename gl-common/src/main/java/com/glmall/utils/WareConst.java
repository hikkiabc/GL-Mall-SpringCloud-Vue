package com.glmall.utils;

public class WareConst {
    public enum PurchaseStatus{
        NEW(0,"New"),
        ALLOCATED(1,"Allocated"),
        ASSIGNED(2,"Assigned"),
        COMPLETE(3,"Complete"),
        FAIL(4,"Fail");
        private Integer code;
        private String name;

        PurchaseStatus(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }
    public enum PurchaseDetailStatus{
        NEW(0,"New"),
        ALLOCATED(1,"Allocated"),
        PURCHASING(2,"Purchasing"),
        COMPLETE(3,"Complete"),
        FAIL(4,"Fail");
        private Integer code;
        private String name;

        PurchaseDetailStatus(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }
}
