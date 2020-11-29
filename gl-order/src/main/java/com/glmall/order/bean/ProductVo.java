package com.glmall.order.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class ProductVo {
    private String id;
    private String name;
    private String description;
    private String categoryId;
    private String brandId;
    private BigDecimal weight;
    private String publishStatus;
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date createTime;
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date updateTime;
    private String brandName;
}
