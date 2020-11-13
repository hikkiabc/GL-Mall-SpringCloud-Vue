package com.glmall.member.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class MemberLevel {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String name;
    private String note;
    private String privilegeMemberPrice;
    private String defaultLevel;
}
