package com.glmall.glproduct.beans;

import com.glmall.utils.validate.AddValidate;
import com.glmall.utils.validate.SortValue;
import com.glmall.utils.validate.UpdateValidate;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Data
public class ProductBrand {
    @Id
    @GeneratedValue(generator = "snowFlakeWorker")
    @GenericGenerator(name = "snowFlakeWorker", strategy = "com.glmall.utils.SnowFlakeWorker")
    private String id;
//    @Pattern(regexp = "")
//    @NotBlank(groups = {AddValidate.class},message = "Name must not be blank")
    private String name;
    //if use group, all the annotations must be grouped
//    @NotBlank(groups = {AddValidate.class})
//    @URL(groups = {UpdateValidate.class, AddValidate.class},message = "must be url")
    private String showStatus;
    @SortValue(value={"0","1"},groups = {UpdateValidate.class})
    private String sort;

    private String logo;
}
