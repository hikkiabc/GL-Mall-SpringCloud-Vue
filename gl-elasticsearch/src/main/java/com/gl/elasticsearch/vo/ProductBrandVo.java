package com.gl.elasticsearch.vo;

import com.glmall.utils.validate.SortValue;
import com.glmall.utils.validate.UpdateValidate;
import lombok.Data;

@Data
public class ProductBrandVo {
    private String id;

    private String name;

    private String showStatus;
    private String sort;

    private String logo;
}
