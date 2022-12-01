package com.zjj.blog.annotation;

import java.lang.annotation.*;

/**
 * @author 知白守黑
 * @date 2022/11/18 20:49
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    /**
     * 操作类型
     */
    String type() default "";
}
