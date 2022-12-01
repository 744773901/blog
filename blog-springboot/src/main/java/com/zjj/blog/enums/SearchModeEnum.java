package com.zjj.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 搜索策略枚举
 *
 * @author 知白守黑
 * @date 2022/11/15 14:35
 */
@Getter
@AllArgsConstructor
public enum SearchModeEnum {

    MYSQL("mysql", "mySqlSearchStrategy"),

    ELASTICSEARCH("elasticsearch", "elasticSearchStrategy");

    /**
     * 模式
     */
    private final String mode;

    /**
     * 策略
     */
    private final String strategy;

    public static String getStrategy(String mode) {
        for (SearchModeEnum modeEnum : SearchModeEnum.values()) {
            if (modeEnum.getMode().equals(mode)) {
                return modeEnum.getStrategy();
            }
        }
        return null;
    }
}
