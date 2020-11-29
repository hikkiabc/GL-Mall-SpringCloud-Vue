package com.glmall.seckill.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
public class SecKillSessionVo {
    private String id;
    private String name;
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date startTime=new Date();
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date endTime=new Date();
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date createTime=new Date();
    private Integer status;
    @Transient
    private List<SecKillSkuRelationVo> secKillSkuRelationList;
}
