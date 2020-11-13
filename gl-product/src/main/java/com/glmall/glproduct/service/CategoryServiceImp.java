package com.glmall.glproduct.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glmall.glproduct.beans.ProductCategory;
import com.glmall.glproduct.beans.vo.Catelog2VO;
import com.glmall.glproduct.dao.BrandCategoryMapper;
import com.glmall.glproduct.dao.CategoryMapper;
import com.glmall.utils.UpdateTool;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
@Transactional
public class CategoryServiceImp implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    private EntityManager em;
    @Autowired
    BrandCategoryMapper brandCategoryMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public List<ProductCategory> getCategoryWithChildren() {

        List<ProductCategory> categoryList = categoryMapper.findAllNotDeleted();
        List<ProductCategory> categoryList1 = categoryList.stream().filter(category -> category.getPid().equals(0 + ""))
                .peek(category -> category.setChildren(setChildren(category, categoryList)))
                .sorted(Comparator.comparingInt(pre -> (pre.getSort() != null ? pre.getSort() : 0)))
                .collect(Collectors.toList());

        return categoryList1;
    }

    @Override
    public Long deleteAll(List list) {
        Integer i = categoryMapper.deleteAllOwn(list);
//        List<Test> tests = testDao.saveAll(list);
//        int j=0;
//        Iterator iterator = list.iterator();
//        int index = 0;
//        while (iterator.hasNext()){
//            em.persist(iterator.next());
//            index++;
//            if (index % 100 == 0){
//                em.flush();
//                em.clear();
//            }
//        }
//        if (index % 100 != 0){
//            em.flush();
//            em.clear();
//        }
        return (long) i;
//        return null;
    }
    @CacheEvict(value={"category"},allEntries = true)
    @Override
    public ProductCategory save(ProductCategory productCategory) {

        if (null != productCategory.getId() && !productCategory.getId().equals("")) {
            ProductCategory productCategory1 = categoryMapper.findById(productCategory.getId()).get();
            if (!StringUtils.isBlank(productCategory.getName())) {
                brandCategoryMapper.save(null, productCategory1.getName(), null, productCategory.getName());
            }
            UpdateTool.copyNullProperties(productCategory1, productCategory);
        }
        return categoryMapper.save(productCategory);
    }

    @Override
    public ProductCategory findCategoryById(String id) {
        return categoryMapper.findById(id).get();
    }

    @Override
    public ProductCategory findById(String categoryId) {
        return categoryMapper.findById(categoryId).get();
    }

    @Cacheable(value = {"category"}, key = "#root.method.name",sync = true)
    @Override
    public List<ProductCategory> findLvl1Category() {
//        long l = System.currentTimeMillis();
        List<ProductCategory> byPid = categoryMapper.findByPid("0");
//        System.out.println(System.currentTimeMillis()-l);
        return byPid;
    }

                                                            //add lock --sync
    @Cacheable(value = {"category"}, key = "#root.methodName",sync = true)
    @Override
    public Map<String, Object> getCategoryMap() {
        System.out.println("dbsearch");
        List<ProductCategory> productCategoryList = categoryMapper.findAll();
        List<ProductCategory> lvl1Category = getByPid(productCategoryList, "0");
        Map<String, Object> productCategoryMap = lvl1Category.stream().collect(Collectors.toMap(
                ProductCategory::getId, e -> {
                    List<ProductCategory> byPid = getByPid(productCategoryList, e.getId());
                    List<Catelog2VO> catelog2VOList = new ArrayList<>();
                    if (byPid != null) {
                        catelog2VOList = byPid.stream().map(lv2 -> {
                            Catelog2VO catelog2VO = new Catelog2VO();
                            catelog2VO.setCatalog1Id(e.getId());
                            catelog2VO.setId(lv2.getId());
                            catelog2VO.setName(lv2.getName());
                            List<ProductCategory> lv3List = getByPid(productCategoryList, lv2.getId());
                            List<Catelog2VO.Catelog3VO> lv3VOList = null;
                            if (lv3List != null) {
                                lv3VOList = lv3List.stream().map(lv3 -> {
                                    Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO();
                                    catelog3VO.setCatalog2Id(lv2.getId());
                                    catelog3VO.setName(lv3.getName());
                                    catelog3VO.setId(lv3.getId());
                                    return catelog3VO;
                                }).collect(Collectors.toList());
                            }
                            catelog2VO.setCatalog3List(lv3VOList);
                            return catelog2VO;
                        }).collect(Collectors.toList());
                    }
                    return catelog2VOList;
                }));
        return productCategoryMap;
    }

    //    @Override
    public Map<String, Object> getCategoryMap_1() throws JsonProcessingException, InterruptedException {
        String getCategoryMap = stringRedisTemplate.opsForValue().get("getCategoryMap");
        ObjectMapper objectMapper = new ObjectMapper();
        if (StringUtils.isBlank(getCategoryMap)) {
//            Map<String, Object> data = getCategoryMapFromDBWithLocalLock();
            Map<String, Object> data = getCategoryMapFromDBWithRedissonLock(objectMapper);
            return data;
        }
        Map<String, Object> readValue = objectMapper.readValue(getCategoryMap, new TypeReference<>() {
        });
        return readValue;
    }

    public Map<String, Object> getCategoryMapFromDBWithRedissonLock(ObjectMapper objectMapper) throws JsonProcessingException {
        String getCategoryMap = stringRedisTemplate.opsForValue().get("getCategoryMap");
        if (StringUtils.isBlank(getCategoryMap)) {
            RLock lock = redissonClient.getLock("getCategoryMap");
            lock.lock(30, TimeUnit.SECONDS);
            Map<String, Object> stringObjectMapFromDB = null;
            try {
                stringObjectMapFromDB = getStringObjectMapFromDB(objectMapper);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            return stringObjectMapFromDB;
        } else {
            return objectMapper.readValue(getCategoryMap, new TypeReference<>() {
            });
        }

    }

    public Map<String, Object> getCategoryMapFromDBWithRedisLock(ObjectMapper objectMapper) throws JsonProcessingException, InterruptedException {
        String getCategoryMap = stringRedisTemplate.opsForValue().get("getCategoryMap");
        if (StringUtils.isBlank(getCategoryMap)) {
            String uuid = UUID.randomUUID().toString();
            Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
            if (lock) {
                Map<String, Object> stringObjectMapFromDB;
                try {
                    stringObjectMapFromDB = getStringObjectMapFromDB(objectMapper);
                } finally {
                    String script = "if redis.call(\"get\",KEYS[1])" +
                            " == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end";
                    stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);
                }
                return stringObjectMapFromDB;
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getCategoryMapFromDBWithRedisLock(objectMapper);
            }

        }
        return objectMapper.readValue(getCategoryMap, new TypeReference<>() {
        });

    }

    public Map<String, Object> getCategoryMapFromDBWithLocalLock() throws JsonProcessingException, InterruptedException {
        synchronized (this) {
            String getCategoryMap = stringRedisTemplate.opsForValue().get("getCategoryMap");
            ObjectMapper objectMapper = new ObjectMapper();
            if (StringUtils.isBlank(getCategoryMap)) {
                return getStringObjectMapFromDB(objectMapper);
            }

            Map<String, Object> readValue = objectMapper.readValue(getCategoryMap, new TypeReference<>() {
            });
            return readValue;
        }

    }

    private Map<String, Object> getStringObjectMapFromDB(ObjectMapper objectMapper) throws JsonProcessingException {
        System.out.println("dbsearch");
        List<ProductCategory> productCategoryList = categoryMapper.findAll();
        List<ProductCategory> lvl1Category = getByPid(productCategoryList, "0");
        Map<String, Object> productCategoryMap = lvl1Category.stream().collect(Collectors.toMap(
                ProductCategory::getId, e -> {
                    List<ProductCategory> byPid = getByPid(productCategoryList, e.getId());
                    List<Catelog2VO> catelog2VOList = new ArrayList<>();
                    if (byPid != null) {
                        catelog2VOList = byPid.stream().map(lv2 -> {
                            Catelog2VO catelog2VO = new Catelog2VO();
                            catelog2VO.setCatalog1Id(e.getId());
                            catelog2VO.setId(lv2.getId());
                            catelog2VO.setName(lv2.getName());
                            List<ProductCategory> lv3List = getByPid(productCategoryList, lv2.getId());
                            List<Catelog2VO.Catelog3VO> lv3VOList = null;
                            if (lv3List != null) {
                                lv3VOList = lv3List.stream().map(lv3 -> {
                                    Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO();
                                    catelog3VO.setCatalog2Id(lv2.getId());
                                    catelog3VO.setName(lv3.getName());
                                    catelog3VO.setId(lv3.getId());
                                    return catelog3VO;
                                }).collect(Collectors.toList());
                            }
                            catelog2VO.setCatalog3List(lv3VOList);
                            return catelog2VO;
                        }).collect(Collectors.toList());
                    }
                    return catelog2VOList;
                }));
        String valueAsString = objectMapper.writeValueAsString(productCategoryMap);
        stringRedisTemplate.opsForValue().set("getCategoryMap", valueAsString);
        return productCategoryMap;
    }

    private List<ProductCategory> getByPid(List<ProductCategory> productCategoryList, String pid) {
        return productCategoryList.stream().filter(e -> e.getPid().equals(pid)).collect(Collectors.toList());
    }

    public void getCategoryChildren(ProductCategory productCategory) {
        List<ProductCategory> byPid = categoryMapper.findByPid(productCategory.getId());
        if (byPid.size() > 0) {
            productCategory.setChildren(byPid);
            byPid.stream().peek(this::getCategoryChildren).collect(Collectors.toList());
        }
    }

    public List<ProductCategory> setChildren(ProductCategory productCategory, List<ProductCategory> list) {
        return list.stream().filter(category -> category.getPid().equals(productCategory.getId()))
                .peek(category -> {
                    category.setChildren(setChildren(category, list));
                }).sorted((pre, next) -> {
                    return (pre.getSort() != null ? pre.getSort() : 0) - (next.getSort() != null ? next.getSort() : 0);
                })
                .collect(Collectors.toList());
    }
}
