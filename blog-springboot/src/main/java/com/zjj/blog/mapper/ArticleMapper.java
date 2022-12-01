package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.dto.*;
import com.zjj.blog.entity.Article;
import com.zjj.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/21 22:46
 */
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 查询后台文章总量
     *
     * @param condition 条件
     * @return 文章总量
     */
    Integer queryArticleCount(@Param("condition") ConditionVO condition);

    /**
     * 查询后台文章
     *
     * @param current   当前页码
     * @param size      页码条数
     * @param condition 查询条件
     * @return 文章列表
     */
    List<AdminArticleDTO> listAdminArticles(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);

    /**
     * 查询首页文章
     *
     * @param current 当前页码
     * @param size    页码条数
     * @return 文章列表
     */
    List<ArticleHomeDTO> selectArticleList(@Param("current") Long current, @Param("size") Long size);

    /**
     * 根据条件查询文章
     *
     * @param current   页码
     * @param size      大小
     * @param condition 条件
     * @return 文章列表
     */
    List<ArticlePreviewDTO> listArticlesByCondition(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);

    /**
     * 根据id查询文章
     *
     * @param id 文章id
     * @return 文章信息
     */
    ArticleDTO selectArticleById(@Param("id") Integer id);

    /**
     * 查询文章的推荐文章
     *
     * @param id 文章id
     * @return 推荐文章列表
     */
    List<ArticleRecommendDTO> listArticleRecommend(@Param("id") Integer id);

    /**
     * 查询文章统计
     *
     * @return 文章统计
     */
    List<ArticleStatisticsDTO> listArticleStatistics();
}
