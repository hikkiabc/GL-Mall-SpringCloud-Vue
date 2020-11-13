package com.glmall.ware.service;

import TO.ProductCombStockTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.glmall.utils.R;
import com.glmall.utils.UpdateTool;
import com.glmall.ware.beans.*;
import com.glmall.ware.feign.MemberFeign;
import com.glmall.ware.feign.WareProductCombFeign;
import com.glmall.ware.mapper.PurchaseDetailMapper;
import com.glmall.ware.mapper.WareInfoMapper;
import com.glmall.ware.mapper.WareProductCombMapper;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WareProductCombServiceImp implements WareProductCombService {
    @Autowired
    WareProductCombMapper wareProductCombMapper;
    @Autowired
    WareInfoMapper wareInfoMapper;
    @Autowired
    PurchaseDetailMapper purchaseDetailMapper;
    @Autowired
    WareProductCombFeign wareProductCombFeign;
    @Autowired
    MemberFeign memberFeign;

    @Override
    public Page<ProductCombWare> getWareProductComb(Map map) {
        Specification<ProductCombWare> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<ProductCombWare> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                String key = (String) map.get("key");
                String productCombId = (String) map.get("productCombId");
                String wareId = (String) map.get("wareId");
                if (!StringUtils.isBlank(productCombId)) {
                    predicates.add(criteriaBuilder.equal(root.get("productCombId"), productCombId));
                }
                if (!StringUtils.isBlank(wareId)) {
                    predicates.add(criteriaBuilder.equal(root.get("wareId"), wareId));
                }

                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        Page<ProductCombWare> productComboWarePage = wareProductCombMapper.findAll(specification, PageRequest.of
                (Integer.valueOf((String) map.get("pageNum")) - 1,
                        Integer.valueOf((String) map.get("pageSize"))));

        return productComboWarePage;
    }

    @Override
    public Page<WareInfo> getWareInfo(Map map, String id) {
        Specification<WareInfo> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<WareInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                String key = (String) map.get("key");
                if (!StringUtils.isBlank(key)) {
                    Predicate id = criteriaBuilder.equal(root.get("id"), key);
                    Predicate name = criteriaBuilder.like(root.get("name"), "%" + key + "%");
                    Predicate address = criteriaBuilder.like(root.get("address"), "%" + key + "%");
                    Predicate areaCode = criteriaBuilder.like(root.get("areaCode"), "%" + key + "%");
                    Predicate or = criteriaBuilder.or(id, name, address, areaCode);
                    predicates.add(or);
                }
                if (!StringUtils.isBlank(id) && !id.equals("0")) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        Page<WareInfo> all = wareInfoMapper.findAll(specification, PageRequest.of
                (Integer.valueOf((String) map.get("pageNum")) - 1,
                        Integer.valueOf((String) map.get("pageSize"))));
        return all;
    }

    @Override
    public WareInfo saveWareInfo(WareInfo wareInfo) {
        if (!StringUtils.isBlank(wareInfo.getId()) && !wareInfo.getId().equals("0")) {
            Example<WareInfo> of = Example.of(wareInfo);
            WareInfo wareInfo1 = wareInfoMapper.findOne(of).get();
            UpdateTool.copyNullProperties(wareInfo1, wareInfo);
        }
        return wareInfoMapper.save(wareInfo);
    }

    @Override
    public Page<PurchaseDetail> getPurchaseDetail(Map map, String id) {
        Specification<PurchaseDetail> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<PurchaseDetail> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                String key = (String) map.get("key");
                String status = (String) map.get("status");
                String wareId = (String) map.get("wareId");
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isBlank(key)) {
                    Predicate purchaseId = criteriaBuilder.equal(root.get("purchaseId"), key);
                    Predicate productCombId = criteriaBuilder.equal(root.get("productCombId"), key);
                    Predicate or = criteriaBuilder.or(productCombId, purchaseId);
                    predicates.add(or);
                }
                if (!StringUtils.isBlank(status)) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                if (!StringUtils.isBlank(wareId)) {
                    predicates.add(criteriaBuilder.equal(root.get("wareId"), wareId));
                }
                if (!StringUtils.isBlank(id) && !id.equals("0")) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        Page<PurchaseDetail> purchaseDetailPage = purchaseDetailMapper.findAll(specification, PageRequest.of
                (Integer.valueOf((String) map.get("pageNum")) - 1,
                        Integer.valueOf((String) map.get("pageSize"))));
        return purchaseDetailPage;
    }

    @Override
    public PurchaseDetail savePurchaseDetail(PurchaseDetail purchaseDetail) {
        if (!StringUtils.isBlank(purchaseDetail.getId()) && !purchaseDetail.getId().equals("0")) {
            PurchaseDetail purchaseDetail1 = purchaseDetailMapper.findById(purchaseDetail.getId()).get();
            UpdateTool.copyNullProperties(purchaseDetail1, purchaseDetail);
        }
        return purchaseDetailMapper.save(purchaseDetail);
    }

    @Override
    public ProductCombWare saveWareProductComb(ProductCombWare productCombWare) {
        String id = productCombWare.getId();
        if (!StringUtils.isBlank(id) && !id.equals("0")) {
            Optional<ProductCombWare> byId = wareProductCombMapper.findById(id);
            UpdateTool.copyNullProperties(byId, productCombWare);
        }
        String productCombId = productCombWare.getProductCombId();

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("key", productCombId);
            map.put("pageSize", 1);
            map.put("pageNum", 1);
            R combination = wareProductCombFeign.getAllCombination(map);
            Map<String, Object> data = (Map<String, Object>) combination.get("data");
            List data1 = (List) data.get("content");
            Map<String, Object> map1 = (Map<String, Object>) data1.get(0);
            String name = (String) map1.get("name");
            productCombWare.setProductCombName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wareProductCombMapper.save(productCombWare);
    }

    @Override
    public List<ProductCombStockTO> getProductCombHasStockByProductCombIdList(List<String> idList) {
        List<ProductCombStockTO> productCombStockTOList = idList.stream().map(e -> {
            Long stockByProductCombId = wareProductCombMapper.getProductCombHasStockByProductCombId(e);
            ProductCombStockTO productCombStockTO = new ProductCombStockTO();
            productCombStockTO.setHasStock(stockByProductCombId == null ? false : stockByProductCombId > 0);
            productCombStockTO.setProductCombId(e);
            return productCombStockTO;
        }).collect(Collectors.toList());
        return productCombStockTOList;
    }

    @Override
    public FareVo getDeliveryFareByAddressId(String id) throws IOException {
        FareVo fareVo = new FareVo();
        R memberAddressById = memberFeign.getMemberAddressById(id);
        MemberAddressVo memberAddressVo= memberAddressById.getData(new TypeReference<MemberAddressVo>() {
        });
        String phone = memberAddressVo.getPhone();
        String substring = phone.substring(phone.length() - 1);
        BigDecimal bigDecimal = new BigDecimal(substring);
        fareVo.setFare(bigDecimal);
        fareVo.setMemberAddressVo(memberAddressVo);
        return fareVo;
    }
}
