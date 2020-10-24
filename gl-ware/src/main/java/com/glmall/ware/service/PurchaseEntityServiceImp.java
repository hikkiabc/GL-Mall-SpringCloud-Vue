package com.glmall.ware.service;

import com.glmall.utils.UpdateTool;
import com.glmall.utils.WareConst;
import com.glmall.ware.beans.PurchaseDetail;
import com.glmall.ware.beans.PurchaseEntity;
import com.glmall.ware.mapper.PurchaseDetailMapper;
import com.glmall.ware.mapper.PurchaseEntityMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseEntityServiceImp implements PurchaseEntityService {
    @Autowired
    PurchaseEntityMapper purchaseEntityMapper;
    @Autowired
    PurchaseDetailMapper purchaseDetailMapper;
    @Override
    public Page<PurchaseEntity> getAllPurchase(Map map, String id) {
        Specification<PurchaseEntity> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<PurchaseEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                String key = (String) map.get("key");
                String status = (String) map.get("status");
                String wareId = (String) map.get("wareId");
                Boolean unreceived = (Boolean) map.get("unreceived");
                if (!StringUtils.isBlank(wareId)) {
                    predicates.add(criteriaBuilder.equal(root.get("wareId"), wareId));
                }
                if (!StringUtils.isBlank(id)&&!id.equals("0")) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                if (!StringUtils.isBlank(status)) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                if (null!=unreceived&&unreceived) {
                    predicates.add(criteriaBuilder.in(root.get("status"))
                            .value(Arrays.asList(WareConst.PurchaseStatus.NEW.getCode(),
                                    WareConst.PurchaseStatus.ALLOCATED.getCode())));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        Page<PurchaseEntity> purchaseEntityPage = purchaseEntityMapper.findAll(specification, PageRequest.of
                (Integer.valueOf((String) map.get("pageNum")) - 1,
                        Integer.valueOf((String) map.get("pageSize"))));

        return purchaseEntityPage;
    }

    @Override
    public PurchaseEntity savePurchaseEntity(PurchaseEntity purchaseEntity) {
        if (!StringUtils.isBlank(purchaseEntity.getId()) && !purchaseEntity.getId().equals("0")) {
            Example<PurchaseEntity> of = Example.of(purchaseEntity);
            PurchaseEntity purchaseEntity1 = purchaseEntityMapper.findById( purchaseEntity.getId()).get();
            UpdateTool.copyNullProperties(purchaseEntity1, purchaseEntity);
        }

        return purchaseEntityMapper.save(purchaseEntity);
    }

    @Override
    public Map<String, Object> mergePurchaseDetail(Map<String, Object> map) {
       List<String> ids= (List) map.get("items");
       String purchaseId= (String) map.get("purchaseId");
       if (StringUtils.isBlank(purchaseId)){
           PurchaseEntity purchaseEntity = new PurchaseEntity();
           PurchaseEntity save = purchaseEntityMapper.save(purchaseEntity);
           purchaseId=save.getId();
       }
        String finalPurchaseId = purchaseId;
        List<PurchaseDetail> collect = ids.stream().map(e -> {
            PurchaseDetail purchaseDetail = purchaseDetailMapper.findById(e).get();
            purchaseDetail.setPurchaseId(finalPurchaseId);
            purchaseDetail.setStatus(
                    WareConst.PurchaseDetailStatus.ALLOCATED.getCode().toString());
            return purchaseDetail;
        }).collect(Collectors.toList());
        List<PurchaseDetail> purchaseDetails = purchaseDetailMapper.saveAll(collect);
        Map map1=new HashMap();
        map1.put("purchaseDetails",purchaseDetails);
        map1.put("purchaseId",purchaseId);
        return map1;
    }
}
