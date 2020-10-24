package com.glmall.member.service;

import com.glmall.member.beans.MemberLevel;
import com.glmall.member.mapper.MemberLevelMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MemberServiceImp implements MemberService {
    @Autowired
    MemberLevelMapper memberLevelMapper;
    @Override
    public Page<MemberLevel> getAllMemberLevel(Map map, String memberLvId) {
        Specification<MemberLevel> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<MemberLevel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates=new ArrayList<>();
                if (!StringUtils.isBlank((String) map.get("key"))) {
                   predicates.add(criteriaBuilder.like(root.get("name"), "%" + map.get("key") + "%")) ;
                }
                if (!StringUtils.isBlank(memberLvId)&&!memberLvId.equals("0")){
                    predicates.add(criteriaBuilder.equal(root.get("id"),memberLvId));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        Page<MemberLevel> memberLevelMapperAll = memberLevelMapper.findAll(specification,PageRequest.of(
                Integer.valueOf((String) map.get("pageNum")) - 1, Integer.valueOf((String) map.get("pageSize"))));
        return memberLevelMapperAll;
    }

    @Override
    public MemberLevel saveMemberLevel(MemberLevel memberLevel) {
        MemberLevel save = memberLevelMapper.save(memberLevel);
        return save;
    }
}
