package com.glmall.coupon.service;

import com.glmall.coupon.TO.ProductDiscountTO;
import com.glmall.coupon.beans.MemberPrice;
import com.glmall.coupon.beans.ProductBound;
import com.glmall.coupon.beans.ProductCombDiscount;
import com.glmall.coupon.beans.ProductCombReduce;
import com.glmall.coupon.mapper.MemberPriceMapper;
import com.glmall.coupon.mapper.ProductBoundMapper;
import com.glmall.coupon.mapper.ProductCombDiscountMapper;
import com.glmall.coupon.mapper.ProductCombReduceMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductDiscountServiceImp implements ProductDiscountService {
    @Autowired
    ProductCombDiscountMapper productCombDiscountMapper;
    @Autowired
    ProductCombReduceMapper productCombReduceMapper;
    @Autowired
    MemberPriceMapper memberPriceMapper;
@Autowired
    ProductBoundMapper productBoundMapper;
    @Override
    public Map<String, Object> saveProduct_discount(ProductDiscountTO productDiscountTO) {
        Map<String,Object> map=new HashMap<>();
        ProductCombDiscount productCombDiscount = new ProductCombDiscount();
        BeanUtils.copyProperties(productDiscountTO,productCombDiscount);
        if (Double.valueOf(productCombDiscount.getFullCount()) >0){
            ProductCombDiscount productCombDiscount1 = productCombDiscountMapper.save(productCombDiscount);
            map.put("productCombDiscount1",productCombDiscount1);
        }
        ProductCombReduce productCombReduce = new ProductCombReduce();
        BeanUtils.copyProperties(productDiscountTO,productCombReduce);
        if(Double.valueOf(productCombReduce.getFullPrice()) >0){
            ProductCombReduce productCombReduce1 = productCombReduceMapper.save(productCombReduce);
            map.put("productCombReduce1",productCombReduce1);
        }
        List<MemberPrice> memberPrice = productDiscountTO.getMemberPrice();
        List<MemberPrice> memberPriceList = memberPrice.stream().map(e -> {
            e.setProductCombId(productDiscountTO.getProductCombId());
            return e;
        }).filter(e1-> e1.getPrice().compareTo(new BigDecimal("0")) >0).collect(Collectors.toList());
        List<MemberPrice> memberPriceList1 = memberPriceMapper.saveAll(memberPriceList);
        map.put("memberPriceList1",memberPriceList1);
        return map;
    }

    @Override
    public ProductBound saveProductBound(ProductBound productBound) {

        return        productBoundMapper.save(productBound);
    }
}
