package com.zjj.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 知白守黑
 * @date 2022/8/13 20:32
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    /**
     * 公开
     */
    PUBLIC(1, "公开"),

    /**
     * 私密
     */
    SECRET(2, "私密"),

    /**
     * 草稿
     */
    DRAFT(3, "草稿");

    /**
     * 状态
     */
    private final Integer status;

    /**
     * 描述
     */
    private final String desc;
}
