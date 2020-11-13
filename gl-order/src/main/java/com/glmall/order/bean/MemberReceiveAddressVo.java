package com.glmall.order.bean;

import lombok.Data;

@Data
public class MemberReceiveAddressVo {
    private String id;
    private String userId;
    private String name;
    private String province;
    private String phone;
    private String detailAddress;
    private String city;
    private String defaultStatus;
}
