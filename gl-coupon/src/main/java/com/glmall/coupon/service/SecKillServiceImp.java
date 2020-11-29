package com.glmall.coupon.service;

import com.glmall.coupon.beans.SecKillSession;
import com.glmall.coupon.beans.SecKillSkuRelation;
import com.glmall.coupon.mapper.SecKillSessionMapper;
import com.glmall.coupon.mapper.SecKillSkuRelationMapper;
import com.glmall.utils.UpdateTool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SecKillServiceImp implements SecKillService {
    @Autowired
    SecKillSessionMapper secKillSessionMapper;
    @Autowired
    SecKillSkuRelationMapper secKillSkuRelationMapper;

    @Override
    public Page<SecKillSession> getSecKillSession(Map<String, Object> params) {
        Specification<SecKillSession> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<SecKillSession> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                String key = (String) params.get("key");
                if (!StringUtils.isBlank(key)) {
                    Predicate name = criteriaBuilder.like(root.get("name"), "%" + key + "%");
                    return name;
                }
                return null;
            }
        };
        PageRequest of = PageRequest.of(Integer.valueOf((String) params.get("pageNum")) - 1,
                Integer.valueOf((String) params.get("pageSize")));
        Page<SecKillSession> killSessionPage = secKillSessionMapper.findAll(specification, of);
        return killSessionPage;
    }

    @Override
    public Page<SecKillSkuRelation> getSecKillSkuRelation(Map<String, Object> params) {
        Specification<SecKillSkuRelation> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<SecKillSkuRelation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                String promotionSessionId = (String) params.get("promotionSessionId");
                if (!StringUtils.isBlank(promotionSessionId)) {
                    Predicate promotionSessionId1 = criteriaBuilder.equal(root.get("promotionSessionId"), promotionSessionId);
                    return promotionSessionId1;
                }
                return null;
            }
        };
        PageRequest of = PageRequest.of(Integer.valueOf((String) params.get("pageNum")) - 1,
                Integer.valueOf((String) params.get("pageSize")));
        Page<SecKillSkuRelation> secKillSkuRelationPage = secKillSkuRelationMapper.findAll(specification, of);
        return secKillSkuRelationPage;
    }

    @Override
    public SecKillSession getSecKillSessionById(String id) {
        SecKillSession secKillSession = secKillSessionMapper.findById(id).get();
        return secKillSession;
    }

    @Override
    public SecKillSkuRelation getSecKillSkuRelationById(String id) {
        SecKillSkuRelation secKillSkuRelation = secKillSkuRelationMapper.findById(id).get();
        return secKillSkuRelation;
    }

    @Override
    public SecKillSession save(SecKillSession secKillSession) {
        if (!StringUtils.isBlank(secKillSession.getId()) && !secKillSession.getId().equals("0")) {
            SecKillSession secKillSession1 = secKillSessionMapper.findById(secKillSession.getId()).get();
            UpdateTool.copyNullProperties(secKillSession1, secKillSession);
            SecKillSession save = secKillSessionMapper.save(secKillSession);
            return save;
        } else {
            SecKillSession save = secKillSessionMapper.save(secKillSession);
            return save;
        }

    }

    @Override
    public SecKillSkuRelation saveSecKillSkuRelation(SecKillSkuRelation secKillSkuRelation) {
        if (!StringUtils.isBlank(secKillSkuRelation.getId()) && !secKillSkuRelation.getId().equals("0")) {
            SecKillSkuRelation secKillSession1 = secKillSkuRelationMapper.findById(secKillSkuRelation.getId()).get();
            UpdateTool.copyNullProperties(secKillSession1, secKillSkuRelation);
            SecKillSkuRelation save = secKillSkuRelationMapper.save(secKillSkuRelation);
            return save;
        } else {
            SecKillSkuRelation save = secKillSkuRelationMapper.save(secKillSkuRelation);
            return save;
        }
    }

    @Override
    public Integer deleteSecKillSession(List<String> ids) {
        Integer integer = secKillSessionMapper.deleteBatchByIds_1(ids);
        return integer;
    }

    @Override
    public Integer deleteSecKillSkuRelation(List<String> ids) {
        Integer integer = secKillSkuRelationMapper.deleteBatchByIds_1(ids);
        return integer;
    }

    @Override
    public List<SecKillSession> getLatest3DaysSecKillProducts() {
        LocalDate now = LocalDate.now();
        LocalDate threeLater = now.plusDays(3);
        LocalDateTime firstDayTime = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime threeDayTime = LocalDateTime.of(threeLater, LocalTime.MAX);
        String format1 = firstDayTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String format3 = threeDayTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<SecKillSession> latest3DaysSecKillSession =
                secKillSessionMapper.findLatest3DaysSecKillSession(format1, format3);
        if (latest3DaysSecKillSession.size()>0){
            List<SecKillSession> collect = latest3DaysSecKillSession.stream().map(secKillSession -> {
                List<SecKillSkuRelation> byPromotionSessionId =
                        secKillSkuRelationMapper.findByPromotionSessionId(secKillSession.getId());
                secKillSession.setSecKillSkuRelationList(byPromotionSessionId);
                return secKillSession;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

}
