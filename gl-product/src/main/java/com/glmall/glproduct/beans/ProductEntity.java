package com.glmall.glproduct.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class ProductEntity {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
    private String name;
    private String description;
    private String categoryId;
    private String brandId;
    private BigDecimal weight;
    private String publishStatus;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date createTime=new Date();
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss"/*, timezone = "GMT+8"*/)
    private Date updateTime=new Date();

//    @OneToMany(targetEntity = DescImg.class,mappedBy = "productProduct",cascade = CascadeType.ALL)
//    private List<DescImg> descImg;
//     @OneToMany(targetEntity = ProductImg.class,mappedBy = "productProduct",cascade = CascadeType.ALL)
//    private List<ProductImg> productImg;

}
