package com.zjj.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 上传模式枚举
 *
 * @author 知白守黑
 * @date 2022/8/7 23:25
 */
@Getter
@AllArgsConstructor
public enum UploadModeEnum {
    /**
     * 本地上传模式
     */
    LOCAL("local", "localUploadStrategy"),

    /**
     * 阿里云OSS上传模式
     */
    OSS("oss", "ossUploadStrategy");

    /**
     * 上传模式
     */
    private String mode;

    /**
     * 上传策略
     */
    private String strategy;

    public static String getStrategy(String mode) {
        for (UploadModeEnum value : UploadModeEnum.values()) {
            if (mode.equals(value.getMode())) {
                return value.getStrategy();
            }
        }
        return null;
    }
}
