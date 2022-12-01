package com.zjj.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author 知白守黑
 * @date 2022/8/27 14:26
 */
@Getter
@AllArgsConstructor
public enum UserAreaEnum {
    /**
     * 用户
     */
    USER(1, "用户"),
    /**
     * 游客
     */
    VISITOR(2, "游客");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 获取用户区域类型
     *
     * @param type 类型
     * @return {@link UserAreaEnum}
     */
    public static UserAreaEnum getUserAreaType(Integer type) {
        for (UserAreaEnum value : UserAreaEnum.values()) {
            if (Objects.equals(value.getType(), type)) {
                return value;
            }
        }
        return null;
    }
}
