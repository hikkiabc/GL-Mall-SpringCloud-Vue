package com.glmall.glproduct.beans.vo;

import lombok.Data;

import java.util.List;

@Data
public class Catelog2VO {
       private String catalog1Id;
       private String id;
       private String name;
       private List<Catelog3VO> catalog3List;


       @Data
       public static class Catelog3VO{
           private String catalog2Id;
           private String id;
           private String name;
       }
}
