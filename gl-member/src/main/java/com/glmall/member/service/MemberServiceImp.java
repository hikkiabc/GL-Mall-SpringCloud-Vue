package com.glmall.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glmall.member.beans.Member;
import com.glmall.member.beans.MemberLevel;
import com.glmall.member.beans.MemberReceiveAddress;
import com.glmall.member.exception.PhoneDuplicatedException;
import com.glmall.member.exception.UserNameDuplicatedException;
import com.glmall.member.mapper.MemberLevelMapper;
import com.glmall.member.mapper.MemberMapper;
import com.glmall.member.mapper.MemberReceiveAddressMapper;
import com.glmall.utils.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MemberServiceImp implements MemberService {
    @Autowired
    MemberLevelMapper memberLevelMapper;
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    MemberReceiveAddressMapper memberReceiveAddressMapper;

    @Override
    public Page<MemberLevel> getAllMemberLevel(Map map, String memberLvId) {
        Specification<MemberLevel> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<MemberLevel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isBlank((String) map.get("key"))) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + map.get("key") + "%"));
                }
                if (!StringUtils.isBlank(memberLvId) && !memberLvId.equals("0")) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), memberLvId));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        Page<MemberLevel> memberLevelMapperAll = memberLevelMapper.findAll(specification, PageRequest.of(
                Integer.valueOf((String) map.get("pageNum")) - 1, Integer.valueOf((String) map.get("pageSize"))));
        return memberLevelMapperAll;
    }

    @Override
    public MemberLevel saveMemberLevel(MemberLevel memberLevel) {
        MemberLevel save = memberLevelMapper.save(memberLevel);
        return save;
    }

    @Override
    public Member register(Member member) {
        checkUserName(member.getUserName());
        checkPhone(member.getPhone());
        MemberLevel byDefaultLevel = memberLevelMapper.findByDefaultLevel("1");
        member.setLevel(byDefaultLevel.getId());
        member.setName(member.getUserName());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        Member save = memberMapper.save(member);

        return save;
    }

    @Override
    public Member login(Member member) {
        Specification<Member> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate username = criteriaBuilder.equal(root.get("userName"), member.getUserName());
                Predicate phone = criteriaBuilder.equal(root.get("phone"), member.getPhone());
                return criteriaBuilder.or(username, phone);
            }
        };
        List<Member> all = memberMapper.findAll(specification);
        if (all.size() > 0) {
            Member member1 = all.get(0);
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            if (bCryptPasswordEncoder.matches(member.getPassword(), member1.getPassword())) {
                return member1;
            }
        }

        return null;
    }

    @Override
    public Member oauthLogin(Member member) throws Exception {
        Member byUid = memberMapper.findByUid(member.getUid());
        if (byUid != null) {
            byUid.setExpires_in(member.getExpires_in());
            byUid.setAccess_token(member.getAccess_token());
            Member save = memberMapper.save(byUid);
            return save;
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", member.getAccess_token());
        paramMap.put("uid", member.getUid());
        HttpResponse get = HttpUtils.doGet("https://api.weibo.com/", "/2/users/show.json",
                "get", new HashMap<>(), paramMap);
        String string = EntityUtils.toString(get.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> readValue = objectMapper.readValue(string, Map.class);
        String name = readValue.get("name");
        String gender = readValue.get("gender");
        member.setName(name);
        member.setGender(gender.equals("m") ? 1 : 0);
        MemberLevel byDefaultLevel = memberLevelMapper.findByDefaultLevel("1");
        member.setLevel(byDefaultLevel.getId());
        Member save = memberMapper.save(member);
        return save;
    }

    @Override
    public List<MemberReceiveAddress> getMemberAddressByMemberId(String id) {
        List<MemberReceiveAddress> byUserId = memberReceiveAddressMapper.getByUserId(id);
        return byUserId;
    }

    @Override
    public MemberReceiveAddress getMemberAddressById(String id) {
        MemberReceiveAddress memberReceiveAddress = memberReceiveAddressMapper.findById(id).get();
        return memberReceiveAddress;

    }

    private void checkPhone(String phone) {
        Long countByPhone = memberMapper.findCountByPhone(phone);
        if (countByPhone > 0) {
            throw new PhoneDuplicatedException();
        }
    }

    private void checkUserName(String userName) {
        Long countByUsername = memberMapper.findCountByUsername(userName);
        if (countByUsername > 0) {
            throw new UserNameDuplicatedException();
        }
    }
}
