package com.zjj.blog.strategy;

import com.zjj.blog.dto.ArticleSearchDTO;

import java.util.List;

/**
 * 搜索策略
 *
 * @author 知白守黑
 * @date 2022/11/15 14:22
 */
public interface SearchStrategy {

    /**
     * 根据关键词搜索文章信息， 由具体实现策略执行
     *
     * @param keyword 关键词
     * @return {@link ArticleSearchDTO}
     */
    List<ArticleSearchDTO> searchArticle(String keyword);
}
