package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.*;
import com.zjj.blog.entity.Article;
import com.zjj.blog.vo.ArticleVO;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.DeleteVO;
import com.zjj.blog.vo.PageResult;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/21 22:37
 */
public interface ArticleService extends IService<Article> {

    /**
     * 点赞文章
     *
     * @param articleId 文章id
     */
    void saveArticleLike(Integer articleId);

    /**
     * 修改文章置顶状态
     *
     * @param articleTop 文章置顶
     */
    void updateArticleTop(ArticleTopDTO articleTop);

    /**
     * 查询后台文章
     *
     * @param condition 条件
     * @return 文章列表
     */
    PageResult<AdminArticleDTO> listAdminArticles(ConditionVO condition);

    /**
     * 逻辑删除文章
     *
     * @param deleteVO 逻辑删除
     */
    void updateArticleLogicDelete(DeleteVO deleteVO);

    /**
     * 物理删除文章
     *
     * @param ids 文章ID
     */
    void deleteArticle(List<Integer> ids);

    /**
     * 根据id查询文章
     *
     * @param id 文章id
     * @return 文章
     */
    ArticleVO getAdminArticleById(Integer id);

    /**
     * 添加或修改文章
     *
     * @param articleVO 文章信息
     */
    void saveOrUpdateArticle(ArticleVO articleVO);

    /**
     * 查询首页文章
     *
     * @return 首页文章列表
     */
    List<ArticleHomeDTO> listArticles();

    /**
     * 查询文章归档
     *
     * @return 文章归档
     */
    PageResult<ArchiveDTO> listArchives();

    /**
     * 通过id查询文章
     *
     * @param id 文章id
     * @return 文章信息
     */
    ArticleDTO getArticleById(Integer id);

    /**
     * 根据条件查询文章列表
     *
     * @param condition 条件
     * @return 文章列表
     */
    ArticlePreviewListDTO listArticlesByCondition(ConditionVO condition);

    /**
     * 搜索文章
     *
     * @param condition 条件
     * @return {@link ArticleSearchDTO}
     */
    List<ArticleSearchDTO> searchArticle(ConditionVO condition);
}
