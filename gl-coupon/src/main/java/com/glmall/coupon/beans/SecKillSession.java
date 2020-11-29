package com.glmall.coupon.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class SecKillSession {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date startTime=new Date();
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date endTime=new Date();
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date createTime=new Date();
    private Integer status;
    @Transient
    private List<SecKillSkuRelation> secKillSkuRelationList;
}
