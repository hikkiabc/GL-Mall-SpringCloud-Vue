package com.glmall.glproduct.service;

import TO.ProductCombTo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.glmall.glproduct.beans.Attribute;
import com.glmall.glproduct.beans.ProductCombImg;
import com.glmall.glproduct.beans.ProductCombination;
import com.glmall.glproduct.beans.ProductDescImg;
import com.glmall.glproduct.beans.vo.*;
import com.glmall.glproduct.config.ThreadPoolConfig;
import com.glmall.glproduct.dao.*;
import com.glmall.glproduct.feign.SecKillFeign;
import com.glmall.utils.BeanUtil;
import com.glmall.utils.R;
import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Transactional
public class SkuServiceImp implements SkuService {
    @Autowired
    ProductCombinationMapper productCombinationMapper;
    @Autowired
    ProductCombImgMapper productCombImgMapper;
    @Autowired
    ProductDescImgMapper productDescImgMapper;
    @Autowired
    AttributeGroupMapper attributeGroupMapper;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    SecKillFeign secKillFeign;

    @Override
    public SkuItemInfoVo findSkuItemInfo(String productCombId) throws ExecutionException, InterruptedException, IOException {
        SkuItemInfoVo skuItemInfoVo = new SkuItemInfoVo();
        System.out.println(productCombId);
        CompletableFuture<ProductCombination> productCombinationFuture = CompletableFuture.supplyAsync(() -> {
            ProductCombination productCombination = productCombinationMapper.findById(productCombId).get();
            skuItemInfoVo.setInfo(productCombination);
            return productCombination;
        }, threadPoolExecutor);

        CompletableFuture<Void> productDesImgFuture = productCombinationFuture.thenAcceptAsync(res -> {
            ProductDescImg byProductId = productDescImgMapper.findByProductId(res.getProductId());
            skuItemInfoVo.setDesc(byProductId);
        }, threadPoolExecutor);

        CompletableFuture<Void> productCombImgFuture = CompletableFuture.runAsync(() -> {
            List<ProductCombImg> byProductCombinationId = productCombImgMapper.findByProductCombinationId(productCombId);
            skuItemInfoVo.setImages(byProductCombinationId);
        }, threadPoolExecutor);


        CompletableFuture<Void> baseAttrFuture = productCombinationFuture.thenAcceptAsync(res -> {
            List<Map<String, Object>> spuGroupAndAttr = attributeGroupMapper.
                    findGroupAndAttrByCateId(res.getCategoryId(), res.getProductId());
            Set<String> groupNameSet = new HashSet<>();
            for (Map<String, Object> map : spuGroupAndAttr) {
                String groupName = (String) map.get("groupName");
                if (!groupNameSet.contains(groupName)) {
                    SpuGroupAndAttrVo spuGroupAndAttrVo = new SpuGroupAndAttrVo();
                    spuGroupAndAttrVo.setGroupName(groupName);
                    Attribute attribute = BeanUtil.mapToEntity(map, Attribute.class);
                    spuGroupAndAttrVo.getBaseAttrs().add(attribute);
                    groupNameSet.add(groupName);
                    skuItemInfoVo.getGroupAttrs().add(spuGroupAndAttrVo);
                } else {
                    Attribute attribute = BeanUtil.mapToEntity(map, Attribute.class);
                    SpuGroupAndAttrVo spuGroupAndAttrVo =
                            skuItemInfoVo.getGroupAttrs().stream().filter(
                                    e -> e.getGroupName().equals(groupName)).collect(Collectors.toList()).get(0);
                    spuGroupAndAttrVo.getBaseAttrs().add(attribute);
                }
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrFuture = productCombinationFuture.thenAcceptAsync(res -> {
            List<Map<String, Object>> skuItemSaleAttrVoList =
                    productCombinationMapper.findProductCombSaleAttrByProductId
                    (res.getProductId());
            Set<String> attrIdSet = new HashSet<>();
            skuItemSaleAttrVoList.stream().map(e -> {
                if (attrIdSet.contains(e.get("attrId").toString())) {
                    SaleAttrValueAndSkuIds saleAttrValueAndSkuIds = new SaleAttrValueAndSkuIds();
                    saleAttrValueAndSkuIds.setAttrValue((String) e.get("attrValue"));
                    saleAttrValueAndSkuIds.setSkuIds(e.get("skuIds").toString());
                    skuItemInfoVo.getSaleAttrs().stream().filter(e1 -> e1.getAttrId().equals(e.get("attrId")))
                            .collect(Collectors.toList()).get(0).getAttrValues().add(saleAttrValueAndSkuIds);
                } else {
                    SkuItemSaleAttrVo skuItemSaleAttrVo = new SkuItemSaleAttrVo();
                    skuItemSaleAttrVo.setAttrId((String) e.get("attrId"));
                    skuItemSaleAttrVo.setAttrName((String) e.get("attrName"));
                    SaleAttrValueAndSkuIds saleAttrValueAndSkuIds = new SaleAttrValueAndSkuIds();
                    saleAttrValueAndSkuIds.setAttrValue((String) e.get("attrValue"));
                    saleAttrValueAndSkuIds.setSkuIds(e.get("skuIds").toString());
                    skuItemSaleAttrVo.getAttrValues().add(saleAttrValueAndSkuIds);
                    skuItemInfoVo.getSaleAttrs().add(skuItemSaleAttrVo);
                    attrIdSet.add((String) e.get("attrId"));
                }
                return null;
            }).collect(Collectors.toList());
        }, threadPoolExecutor);

        CompletableFuture<Void> secKillFuture = CompletableFuture.runAsync(() -> {
            R skuSecKillInfo = secKillFeign.getSkuSecKillInfo(productCombId);
            SecKillSkuRelationVo secKillSkuRelationVo = null;
            try {
                secKillSkuRelationVo = skuSecKillInfo.getData(new TypeReference<>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            skuItemInfoVo.setSeckillInfo(secKillSkuRelationVo);
        },threadPoolExecutor);

        CompletableFuture.allOf(saleAttrFuture, baseAttrFuture, productDesImgFuture, productCombImgFuture,secKillFuture).get();
        return skuItemInfoVo;
    }

    @Override
    public ProductCombination getProductCombByProductCombId(String id) {
        ProductCombination productCombination = productCombinationMapper.findById(id).get();
        return productCombination;
    }
}
