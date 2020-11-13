package com.glmall.cart.to;


import lombok.Data;

@Data
public class UserOrTempUserTo {
    private String userId;
    private String tempUserKey;
    private Boolean temp=false;
}
