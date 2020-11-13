package com.glmall.glproduct.beans.vo;

import com.glmall.glproduct.beans.Attribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpuGroupAndAttrVo {
    private String groupName;
    private List<Attribute> baseAttrs=new ArrayList<>();
}
