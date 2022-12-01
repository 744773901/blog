package com.zjj.blog.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/15 22:13
 */
public class BeanCopyUtil {

    /**
     * 复制对象
     *
     * @param source 源
     * @param target 目标
     * @return {@link T}
     */
    public static <T> T copyObject(Object source, Class<T> target) {
        T t = null;
        try {
            t = target.newInstance();
            if (null != source) {
                BeanUtils.copyProperties(source, t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 复制集合
     * @param source 源
     * @param target 目标
     * @return {@link List<T>} 集合
     */
    public static <S, T> List<T> copyList(List<S> source, Class<T> target) {
        List<T> list = new ArrayList<>();
        if (null != source && source.size() > 0) {
            for (S s : source) {
                list.add(copyObject(s, target));
            }
        }
        return list;
    }
}
