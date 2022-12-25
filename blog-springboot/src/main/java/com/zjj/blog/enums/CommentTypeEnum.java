package com.zjj.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论类型枚举
 */
@Getter
@AllArgsConstructor
public enum CommentTypeEnum {

    /**
     * 文章评论
     */
    ARTICLE(1, "文章评论", "/articles/"),
    /**
     * 友链评论
     */
    LINK(1, "友链评论", "/links/"),
    /**
     * 说说评论
     */
    TALK(1, "说说评论", "/talks/");

    /**
     * 评论类型
     */
    private final Integer type;
    /**
     * 描述
     */
    private final String desc;
    /**
     * 路径
     */
    private final String path;

    /**
     * 获取评论枚举
     *
     * @param type 评论类型
     * @return {@link CommentTypeEnum} 评论枚举
     */
    public static CommentTypeEnum getCommentType(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (commentTypeEnum.getType().equals(type))
                return commentTypeEnum;
        }
        return null;
    }
}
