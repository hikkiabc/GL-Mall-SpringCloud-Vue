package com.glmall.ware.beans;

import lombok.Data;

@Data
public class MemberAddressVo {
    private String id;
    private String userId;
    private String name;
    private String province;
    private String phone;
    private String detailAddress;
    private String city;
    private String defaultStatus;
}
