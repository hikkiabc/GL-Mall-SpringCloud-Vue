package com.glmall.member.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Member {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String name;
    private String userName;
    private String phone;
    private String level;
    private String password;
    private String access_token;
    private String expires_in;
    private String uid;
    private Integer gender;
    private Integer points;
}
