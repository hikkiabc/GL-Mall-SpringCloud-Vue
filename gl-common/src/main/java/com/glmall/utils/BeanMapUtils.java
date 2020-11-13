package com.glmall.utils;

import org.springframework.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BeanMapUtils {

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /**
     * 将对象属性转化为map结合
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key+"", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map集合中的数据转化为指定对象的同名属性中
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) throws Exception {
        Map<String, Object> newMap = new HashMap<>();
        for(Map.Entry<String, Object> entry:map.entrySet()){
            newMap.put(lineToHump(entry.getKey()),entry.getValue());
        }
        T bean = clazz.newInstance();
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(newMap);
        return bean;
    }

    /** 下划线转驼峰 */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
