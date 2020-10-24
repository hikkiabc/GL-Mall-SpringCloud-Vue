package com.glmall.glproduct.controller;

import com.glmall.glproduct.beans.Attribute;
import com.glmall.glproduct.beans.AttributeGroup;
import com.glmall.glproduct.beans.Attribute_AttributeGroup;
import com.glmall.glproduct.beans.Product_Attribute;
import com.glmall.glproduct.beans.vo.AttributeVO;
import com.glmall.glproduct.beans.vo.GroupWithAttrVO;
import com.glmall.glproduct.service.AttributeService;
import com.glmall.utils.R;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Attr;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product/attr")
public class AttributeController {
    @Autowired
    AttributeService attributeService;

    @GetMapping("/group/{cateId}")
    public R getAllAttrGroup(@RequestParam Map map, @PathVariable("cateId") String cateId) {
//        System.out.println(map);
        Page allAttrGroup = attributeService.getAllAttrGroup(map, cateId);
        System.out.println(allAttrGroup);
        return R.ok().put("data", allAttrGroup);
    }

    @PostMapping("/group")
    public R saveOrUpdateAttributeGroup(@RequestBody AttributeGroup attributeGroup) {
        System.out.println(attributeGroup);
        AttributeGroup attributeGroup1 = attributeService.save(attributeGroup);
        return R.ok().put("data", attributeGroup1);
    }

    @PostMapping("/attribute")
    public R saveAttribute(@RequestBody AttributeVO attributeVO) {
        System.out.println(attributeVO);
        Map map = attributeService.save(attributeVO);
        return R.ok().put("data", map);
    }

    @GetMapping("/attrgroup-with-attr/{categoryId}")
    public R getGroupWithAttr(@PathVariable String categoryId) {
        List<GroupWithAttrVO> groupWithAttrList = attributeService.getGroupWithAttr(categoryId);
        return R.ok().put("data", groupWithAttrList);
    }

    @GetMapping("/attribute/{categoryId}")
    public R getAllAttribute(@RequestParam Map map, @PathVariable String categoryId) {
        Page attributePage = attributeService.getAllAttribute(map, categoryId);
        return R.ok().put("data", attributePage);
    }

    @DeleteMapping("/attribute_attributeGroup")
    public R deleteAttribute_attributeGroup(@RequestBody List<Attribute_AttributeGroup> attribute_attributeGroupList) {
        Integer integer = attributeService.deleteAttribute_attributeGroup(attribute_attributeGroupList);
        return R.ok().put("data", integer);
    }

    @PostMapping("/attribute_attributeGroup")
    public R saveAttribute_attributeGroup(@RequestBody List<Attribute_AttributeGroup> attribute_attributeGroupList) {
        List<Attribute_AttributeGroup> attribute_attributeGroups = attributeService.saveAttribute_attributeGroup(attribute_attributeGroupList);
        return R.ok().put("data", attribute_attributeGroups);
    }

    @GetMapping("/attribute/unrealated")
    public R getUnrelatedAttrByGroupId(@RequestParam Map map) {
        Page<AttributeVO> attributeVOPage = attributeService.getUnrelatedAttrByGroupId(map);
        return R.ok().put("data", attributeVOPage);
    }

    @GetMapping("/product-basic-attr/{categoryId}")
    public R getProductBasicAttr(@PathVariable String categoryId){
      List<Product_Attribute> product_attributeList=  attributeService.getProductBasicAttr(categoryId);
      return R.ok().put("data",product_attributeList);
    }
    @PostMapping("/product-basic-attr/{productId}")
    public  R saveProductBasicAttr(@RequestBody List<Product_Attribute> product_attributes, @PathVariable String productId){
       List< Product_Attribute>  product_attributeList= attributeService.saveProductBasicAttr(product_attributes,productId);
       return R.ok().put("data",product_attributeList);
    }

}
