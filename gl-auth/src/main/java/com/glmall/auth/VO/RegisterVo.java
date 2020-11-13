package com.glmall.auth.VO;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class RegisterVo {

    @Length(min = 2,max=10,message = "Not Empty length wrong")
    private String userName;
    private String password;
    private String code;
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "Not correct phone")
    private String phone;
    private String access_token;
    private String expires_in;
    private String uid;
    private Integer gender;
}
