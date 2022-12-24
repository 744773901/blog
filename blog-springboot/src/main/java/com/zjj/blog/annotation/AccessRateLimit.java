package com.zjj.blog.annotation;


import java.lang.annotation.*;

/**
 * @author 知白守黑
 * @description 限流注解(限制接口在单位时间内的最大请求次数)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessRateLimit {

    /**
     * 单位时间(秒)
     *
     * @return second
     */
    int second();

    /**
     * 单位时间内最大请求次数
     *
     * @return counter
     */
    int counter();
}
