package com.glmall.glproduct.beans;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
public class ProductImg {
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.glmall.utils.SnowflakeId")
    private String id;
    private String defaultImg;
    private String imgUrl;
    private String imgName;
    private String productId;

}
