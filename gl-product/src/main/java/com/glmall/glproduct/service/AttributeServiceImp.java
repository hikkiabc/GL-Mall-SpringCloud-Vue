package com.glmall.glproduct.service;

import com.glmall.glproduct.beans.*;
import com.glmall.glproduct.beans.vo.AttributeVO;
import com.glmall.glproduct.beans.vo.GroupWithAttrVO;
import com.glmall.glproduct.dao.*;
import com.glmall.utils.Const;
import com.glmall.utils.Page_1;
import com.glmall.utils.UpdateTool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttributeServiceImp implements AttributeService {
    @Autowired
    AttributeGroupMapper attributeGroupMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    AttributeMapper attributeMapper;
    @Autowired
    Attribute_AttributeGroupMapper attribute_attributeGroupMapper;
    @Autowired
    EntityManager entityManager;
    @Autowired
    Product_AttributeMapper product_attributeMapper;
    @Autowired
    ProductComb_AttributeMapper productComb_attributeMapper;

    @Override
    public Page getAllAttrGroup(Map map, String cateId) {
        System.out.println(map);
        if (null != map.get("id")) {
            String id = String.valueOf(map.get("id"));
            AttributeGroup attributeGroup = attributeGroupMapper.findById(id).get();
            List<String> categoryList = new ArrayList<>();
            List<AttributeGroup> attributeGroups = new ArrayList<>();
            getAllCategoryParentIds(categoryList, attributeGroup.getCateId());
            Collections.reverse(categoryList);
            attributeGroup.setCategoryIds(categoryList);
            attributeGroups.add(attributeGroup);
            return new PageImpl<>(attributeGroups, PageRequest.of(0, 1), 1);
        }
        Specification<AttributeGroup> specification = (Specification<AttributeGroup>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String key = (String) map.get("key");
            if (!StringUtils.isBlank(key)) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + key + "%"));
            }
//                Set idSet = new HashSet<>();
//                idSet.add("1");
//                idSet.add("2");
//                CriteriaBuilder.In idIn = criteriaBuilder.in(root.get("id")).value(idSet);
//                Predicate equal = criteriaBuilder.equal(root.get("icon"), "iconValue");
//                predicates.add(criteriaBuilder.or(idIn, equal));
            // 'and' seems can be remove before 'or'
//                predicates.add(criteriaBuilder.and(criteriaBuilder.or(idIn, equal)));

            if (!cateId.equals("0")) {
                predicates.add(criteriaBuilder.equal(root.get("cateId"), cateId));
            }
            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
            return criteriaQuery.getRestriction();
        };
        PageRequest pageRequest = PageRequest.of(Integer.valueOf((String) map.get("page")) - 1, Integer.valueOf((String) map.get("limit")));

        Page<AttributeGroup> attributeGroups = attributeGroupMapper.findAll(specification, pageRequest);
        return attributeGroups;
    }

    @Override
    public AttributeGroup save(AttributeGroup attributeGroup) {

        return attributeGroupMapper.save(attributeGroup);
    }

    @Override
    public Map save(AttributeVO attributeVO) {
        Map<String, Object> map = new HashMap();
        Attribute_AttributeGroup attribute_attributeGroup;
        if (!StringUtils.isBlank(attributeVO.getId())) {
            Attribute attribute = attributeMapper.findById(attributeVO.getId()).get();
            UpdateTool.copyNullProperties(attribute, attributeVO);
            if (!StringUtils.isBlank(attributeVO.getGroupId())) {
                if (attributeVO.getType().equals(Const.AttributeEnum.BASE_ATTRIBUTE.getCode())
                        && !attributeVO.getType().equals(attribute.getType())) {
                    attribute_attributeGroup = new Attribute_AttributeGroup();
                    attribute_attributeGroup.setAttributeId(attributeVO.getId());
                    attribute_attributeGroup.setAttributeGroupId(attributeVO.getGroupId());
                    attribute_attributeGroupMapper.save(attribute_attributeGroup);
                    map.put("save attribute_attributeGroup", attribute_attributeGroup);

                }
                if (attributeVO.getType().equals(Const.AttributeEnum.BASE_ATTRIBUTE.getCode())
                        && attributeVO.getType().equals(attribute.getType())) {
                    attribute_attributeGroup =
                            attribute_attributeGroupMapper.findByAttributeId(attributeVO.getId());
                    if (attribute_attributeGroup == null) {
                        attribute_attributeGroup = new Attribute_AttributeGroup();
                    }
                    attribute_attributeGroup.setAttributeId(attributeVO.getId());
                    attribute_attributeGroup.setAttributeGroupId(attributeVO.getGroupId());
                    attribute_attributeGroupMapper.save(attribute_attributeGroup);
                    map.put("save attribute_attributeGroup", attribute_attributeGroup);
                }
            } else {
                Integer integer = attribute_attributeGroupMapper.deleteByAttributeId(attribute.getId());
                map.put("delete attribute_attributeGroupMapper count", integer);
            }
            if (attributeVO.getType().equals(Const.AttributeEnum.SALE_ATTRIBUTE.getCode())) {
                if (!attribute.getType().equals(attributeVO.getType())) {
                    Integer integer = attribute_attributeGroupMapper.deleteByAttributeId(attribute.getId());
                    map.put("delete attribute_attributeGroupMapper count", integer);
                }
            }
        }
        Attribute attribute1 = new Attribute();
        BeanUtils.copyProperties(attributeVO, attribute1);
        Attribute save = attributeMapper.save(attribute1);
        if (attributeVO.getType().equals(Const.AttributeEnum.BASE_ATTRIBUTE.getCode())
                && StringUtils.isBlank(attributeVO.getId()) && !StringUtils.isBlank(attributeVO.getGroupId())) {
            attribute_attributeGroup = new Attribute_AttributeGroup();
            attribute_attributeGroup.setAttributeGroupId(attributeVO.getGroupId());
            attribute_attributeGroup.setAttributeId(save.getId());
            attribute_attributeGroupMapper.save(attribute_attributeGroup);
            map.put("save attribute_attributeGroup", attribute_attributeGroup);

        }
        map.put("save", save);
        return map;
    }
// the result is changing according to different id, so redis is not suitable?
//    @Cacheable(value = "attr", key = "'attrInfoById'")
    @Override
    public Page_1 getAllAttribute(Map map, String categoryId) {
        String id = (String) map.get("id");
        String type = (String) map.get("type");
        String key = (String) map.get("key");
        String groupId = (String) map.get("groupId");
        PageRequest pageRequest;
        Specification<Attribute> specification;
        if (!StringUtils.isBlank(id)) {
            specification = new Specification<>() {
                @Override
                public Predicate toPredicate(Root<Attribute> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate predicate = criteriaBuilder.equal(root.get("id"), id);
                    return criteriaQuery.where(predicate).getRestriction();
                }
            };
            pageRequest = PageRequest.of(0, 1);
        } else {
            specification = new Specification<>() {
                @Override
                public Predicate toPredicate(Root<Attribute> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (!StringUtils.isBlank(type)) {
                        predicates.add(criteriaBuilder.equal(root.get("type"), type));
                    }
                    if (!StringUtils.isBlank(groupId)) {
                        List<Attribute_AttributeGroup> byAttributeGroupId = attribute_attributeGroupMapper.findByAttributeGroupId(groupId);
                        List<String> attributeIdList = byAttributeGroupId.stream().map(Attribute_AttributeGroup::getAttributeId).collect(Collectors.toList());
                        CriteriaBuilder.In id1 = criteriaBuilder.in(root.get("id")).value(attributeIdList);
                        predicates.add(id1);
                    }
                    if (!StringUtils.isBlank(key)) {
                        predicates.add(criteriaBuilder.like(root.get("name"), "%" + key + "%"));
                    }
                    if (!categoryId.equals("0")) {
                        predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
                    }
                    return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
                }
            };
            pageRequest = PageRequest.of(
                    Integer.valueOf((String) map.get("pageNum")) - 1,
                    Integer.valueOf((String) map.get("pageSize")));
        }

        Page<Attribute> attributePage = attributeMapper.findAll(specification, pageRequest);
        List<AttributeVO> resList = attributePage.getContent().stream().map(e -> {
            AttributeVO attributeVO = new AttributeVO();
            BeanUtils.copyProperties(e, attributeVO);
            if (!StringUtils.isBlank(e.getCategoryId())) {
                attributeVO.setCategoryName(categoryMapper.findById(e.getCategoryId()).get().getName());
                List<String> categoryIds = new ArrayList<>();
                getAllCategoryParentIds(categoryIds, e.getCategoryId());
                Collections.reverse(categoryIds);
                attributeVO.setCategoryIds(categoryIds);
            }
            if (Const.AttributeEnum.BASE_ATTRIBUTE.getCode().equals(e.getType())) {
                Attribute_AttributeGroup byAttributeId = attribute_attributeGroupMapper.findByAttributeId(e.getId());
                if (byAttributeId != null && !StringUtils.isBlank(byAttributeId.getAttributeGroupId())) {
                    AttributeGroup attributeGroup =
                            attributeGroupMapper.findById(byAttributeId.getAttributeGroupId()).get();
                    attributeVO.setGroupName(attributeGroup.getName());
                    attributeVO.setGroupId(attributeGroup.getId());
                }
            }
            return attributeVO;
        }).collect(Collectors.toList());
        return new Page_1(resList, attributePage.getNumber(),
                attributePage.getSize(), attributePage.getTotalElements());
    }

    @Override
    public Integer deleteAttribute_attributeGroup(List<Attribute_AttributeGroup> attribute_attributeGroupList) {

        if (attribute_attributeGroupList.size() > 0) {
            // delete bulk method 1:

            StringBuffer sql = new StringBuffer("delete from attribute_attribute_group where ");
            for (int i = 0; i < attribute_attributeGroupList.size(); i++) {
                if (i != 0) {
                    sql.append(" or ");
                }
                sql.append("(attribute_group_id=" + attribute_attributeGroupList.get(i).getAttributeGroupId()
                        + " and attribute_id=" + attribute_attributeGroupList.get(i).getAttributeId() + ")");
            }
            Query nativeQuery = entityManager.createNativeQuery(sql.toString());
            int i = nativeQuery.executeUpdate();

            // delete bulk method 2:

//            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//            List<Predicate> predicates=new ArrayList<>();
//            CriteriaDelete<Attribute_AttributeGroup> delete = cb.createCriteriaDelete(Attribute_AttributeGroup.class);
//            Root<Attribute_AttributeGroup> root = delete.from(Attribute_AttributeGroup.class);
//            for (Attribute_AttributeGroup attribute_attributeGroup : attribute_attributeGroupList) {
//                Predicate predicate = cb.equal(root.get("attributeGroupId"), attribute_attributeGroup.getAttributeGroupId());
//                Predicate predicate1 = cb.equal(root.get("attributeId"), attribute_attributeGroup.getAttributeId());
//                Predicate and = cb.and(predicate, predicate1);
//                predicates.add(and);
//            }
//            Predicate or = cb.or(predicates.toArray(new Predicate[0]));
//            delete.where(or);
//            int i = entityManager.createQuery(delete).executeUpdate();

            return i;
        } else return 0;

    }

    @Override
    public Page<AttributeVO> getUnrelatedAttrByGroupId(Map map) {
        String key = (String) map.get("key");
        String groupId = (String) map.get("groupId");
        AttributeGroup attributeGroup = attributeGroupMapper.findById(groupId).get();
        List<AttributeGroup> groupListByCateId = attributeGroupMapper.findByCateId(attributeGroup.getCateId());
        List<String> groupIds = groupListByCateId.stream().map(AttributeGroup::getId).collect(Collectors.toList());
        List<Attribute_AttributeGroup> byAttributeGroupIdIn = attribute_attributeGroupMapper.findByAttributeGroupIdIn(groupIds);
        List<String> attributeIds = byAttributeGroupIdIn.stream().map(Attribute_AttributeGroup::getAttributeId).collect(Collectors.toList());
        Specification<Attribute> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<Attribute> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isBlank(key)) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + key + "%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), attributeGroup.getCateId()));
                predicates.add(criteriaBuilder.notEqual(root.get("type"), Const.AttributeEnum.SALE_ATTRIBUTE.getCode()));
                if (attributeIds.size() > 0) {
                    Predicate id1 = criteriaBuilder.in(root.get("id")).value(attributeIds).not();
                    predicates.add(id1);
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        PageRequest of = PageRequest.of(Integer.valueOf((String) map.get("pageNum")) - 1, Integer.valueOf((String) map.get("pageSize")));
        Page<Attribute> all = attributeMapper.findAll(specification, of);
        List<AttributeVO> attributeVOList = all.stream().map(e -> {
            AttributeVO attributeVO = new AttributeVO();
            BeanUtils.copyProperties(e, attributeVO);
            return attributeVO;
        }).collect(Collectors.toList());

        return new PageImpl(attributeVOList, PageRequest.of(all.getNumber(), all.getSize()), all.getTotalElements());
    }

    @Override
    public List<Attribute_AttributeGroup> saveAttribute_attributeGroup(List<Attribute_AttributeGroup> attribute_attributeGroupList) {
//        SnowFlakeWorker snowFlakeWorker = new SnowFlakeWorker();
//        for (Attribute_AttributeGroup attribute_attributeGroup : attribute_attributeGroupList) {
//            attribute_attributeGroup.setId(snowFlakeWorker.nextId()+"");
//        }
//        attribute_attributeGroupMapper.saveAll(attribute_attributeGroupList);
        //primary key problem, saveAll wont work unless setId manually 
        for (Attribute_AttributeGroup attribute_attributeGroup : attribute_attributeGroupList) {
            entityManager.persist(attribute_attributeGroup);
            entityManager.flush();
            entityManager.clear();
        }

        return null;
    }

    @Override
    public List<GroupWithAttrVO> getGroupWithAttr(String categoryId) {
        List<AttributeGroup> byCateId = attributeGroupMapper.findByCateId(categoryId);
        List<GroupWithAttrVO> groupWithAttrVOS = byCateId.stream().map(e -> {
            GroupWithAttrVO groupWithAttrVO = new GroupWithAttrVO();
            BeanUtils.copyProperties(e, groupWithAttrVO);
            List<Attribute_AttributeGroup> attribute_attributeGroupList
                    = attribute_attributeGroupMapper.findByAttributeGroupId(e.getId());
            List<String> attrIds = attribute_attributeGroupList.stream()
                    .map(attribute_attributeGroup -> attribute_attributeGroup.getAttributeId())
                    .collect(Collectors.toList());
            List<Attribute> attributeList = attributeMapper.findAllById(attrIds);
            groupWithAttrVO.setAttributeList(attributeList);
            return groupWithAttrVO;
        }).collect(Collectors.toList());
        return groupWithAttrVOS;
    }

    @Override
    public List<Product_Attribute> getProductBasicAttr(String categoryId) {
        List<Product_Attribute> categoryId1 = product_attributeMapper.findByProductId(categoryId);
        return categoryId1;
    }

    @Override
    public List<Product_Attribute> saveProductBasicAttr(List<Product_Attribute> product_attributes, String productId) {
        Integer integer = product_attributeMapper.deleteByProductId(productId);
        List<Product_Attribute> product_attributes1 = product_attributes.stream().map(e -> {
            e.setProductId(productId);
            return e;
        }).collect(Collectors.toList());
        List<Product_Attribute> product_attributeList = product_attributeMapper.saveAll(product_attributes1);
        return product_attributeList;
    }

    @Override
    public List<String> getSkuSaleAttrsBySkuId(String id) {
        List<String> skuSaleAttrsBySkuId = productComb_attributeMapper.getSkuSaleAttrsBySkuId(id);
        return skuSaleAttrsBySkuId;
    }

    private void getAllCategoryParentIds(List categoryList, String id) {
        categoryList.add(id);
        ProductCategory productCategory = categoryMapper.findById(id).get();
        if (!productCategory.getPid().equals("0")) {
            getAllCategoryParentIds(categoryList, productCategory.getPid());
        }

    }
}
