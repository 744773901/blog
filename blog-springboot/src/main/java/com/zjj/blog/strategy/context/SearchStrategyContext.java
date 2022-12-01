package com.zjj.blog.strategy.context;

import com.zjj.blog.dto.ArticleSearchDTO;
import com.zjj.blog.enums.SearchModeEnum;
import com.zjj.blog.strategy.SearchStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 知白守黑
 * @date 2022/11/15 14:45
 */
@Component
public class SearchStrategyContext {

    /**
     * 搜索模式
     */
    @Value("${search.mode}")
    private String mode;

    @Resource
    private Map<String, SearchStrategy> searchStrategyMap;

    /**
     * 执行具体搜索策略
     *
     * @param keyword 搜索关键词
     * @return {@link ArticleSearchDTO}
     */
    public List<ArticleSearchDTO> executeSearchArticle(String keyword) {
        return searchStrategyMap.get(SearchModeEnum.getStrategy(mode)).searchArticle(keyword);
    }
}
