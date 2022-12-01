package com.zjj.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件路径枚举
 *
 * @author 知白守黑
 * @date 2022/8/7 23:43
 */
@Getter
@AllArgsConstructor
public enum FilePathEnum {

    /**
     * 头像路径
     */
    AVATAR("avatar/", "头像路径"),

    /**
     * 文章图片路径
     */
    ARTICLE("article/", "文章图片路径"),

    /**
     * 音频路径
     */
    VOICE("voice/", "音频路径"),

    /**
     * 照片路径
     */
    PHOTO("photo/", "相册路径"),

    /**
     * 配置图片路径
     */
    CONFIG("config/", "配置图片路径"),

    /**
     * 说说图片路径
     */
    TALK("talk/", "配置图片路径");

    /**
     * 路径
     */
    private String path;

    /**
     * 描述
     */
    private String desc;
}
