package com.zjj.blog.service;

import com.zjj.blog.dto.AdminBlogInfoDTO;
import com.zjj.blog.dto.BlogHomeInfoDTO;
import com.zjj.blog.vo.AboutVO;
import com.zjj.blog.vo.WebsiteConfigVO;

/**
 * @author 知白守黑
 * @date 2022/8/23 22:04
 */
public interface BlogInfoService {

    /**
     * 获取网站配置
     *
     * @return 网站配置
     */
    WebsiteConfigVO getWebsiteConfig();

    /**
     * 修改网站配置
     *
     * @param websiteConfigVO 网站配置信息
     */
    void updateWebsiteConfig(WebsiteConfigVO websiteConfigVO);

    /**
     * 查询Redis中关于我内容
     *
     * @return 关于我内容
     */
    String getAbout();

    /**
     * 修改关于我内容
     *
     * @param aboutVO 关于我内容
     */
    void updateAbout(AboutVO aboutVO);

    /**
     * 上传访客信息
     */
    void report();

    /**
     * 查看博客信息
     *
     * @return 博客信息
     */
    BlogHomeInfoDTO getBlogHomeInfo();

    /**
     * 获取后台信息
     *
     * @return 后台信息
     */
    AdminBlogInfoDTO getAdminBlogInfo();
}
