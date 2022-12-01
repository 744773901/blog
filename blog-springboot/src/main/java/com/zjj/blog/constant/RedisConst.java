package com.zjj.blog.constant;

/**
 * redis常量
 *
 * @author 知白守黑
 * @date 2022/7/21 22:15
 */
public class RedisConst {

    /**
     * 登录用户ID
     */
    public static final String LOGIN_USER_KEY = "login_user_key:";

    /**
     * 文章点赞
     */
    public static final String USER_ARTICLE_LIKE = "user_article-like:";
    /**
     * 文章点赞量
     */
    public static final String ARTICLE_LIKE_COUNT = "article_like_count";
    /**
     * 评论点赞
     */
    public static final String USER_COMMENT_LIKE = "user_comment_like:";
    /**
     * 评论点赞量
     */
    public static final String COMMENT_LIKE_COUNT = "comment_like_count";
    /**
     * 说说点赞
     */
    public static final String USER_TALK_LIKE = "user_talk_like:";
    /**
     * 说说点赞量
     */
    public static final String TALK_LIKE_COUNT = "talk_like_count";
    /**
     * 文章浏览量
     */
    public static final String ARTICLE_VIEWS_COUNT = "article_views_count";
    /**
     * 博客浏览量
     */
    public static final String BLOG_VIEWS_COUNT = "blog_views_count";
    /**
     * 网站配置
     */
    public static final String WEBSITE_CONFIG = "website_config";
    /**
     * 页面封面
     */
    public static final String PAGE_COVER = "page_cover";
    /**
     * 关于我
     */
    public static final String ABOUT = "about";
    /**
     * 访客
     */
    public static final String UNIQUE_VISITOR = "unique_visitor";
    /**
     * 用户地区
     */
    public static final String USER_AREA = "user_area";

    /**
     * 访客地区
     */
    public static final String VISITOR_AREA = "visitor_area";

    /**
     * 验证码
     */
    public static final String CODE_KEY = "code:";

    /**
     * 验证码过期时间
     */
    public static final long CODE_EXPIRE_TIME = 15 * 60;
}
