package com.glmall.member.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class MemberReceiveAddress {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String userId;
    private String name;
    private String province;
    private String phone;
    private String detailAddress;
    private String city;
    private String defaultStatus;
}
