package com.zjj.blog.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 知白守黑
 * @date 2022/8/29 19:17
 */
public class CommonUtil {

    /**
     * 转Set
     *
     * @param obj   {@link Object}
     * @param clazz {@link Class<T>}
     * @param <T>   t
     * @return {@link Set<T>}
     */
    public static <T> Set<T> toSet(Object obj, Class<T> clazz) {
        Set<T> set = new HashSet<>();
        if (obj instanceof Set<?>) {
            ((Set<?>) obj).forEach(o -> set.add(clazz.cast(o)));
            return set;
        }
        return set;
    }

    /**
     * 检查邮箱格式
     *
     * @param username 邮箱用户名
     * @return true or false
     */
    public static boolean isEmail(String username) {
        String regex = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    /**
     * 生成六位随机数字验证码
     *
     * @return 验证码
     */
    public static String randomCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
