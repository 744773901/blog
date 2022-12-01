package com.zjj.blog.strategy.search;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjj.blog.dto.ArticleSearchDTO;
import com.zjj.blog.strategy.SearchStrategy;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.*;
import static com.zjj.blog.enums.StatusEnum.PUBLIC;

/**
 * @author 知白守黑
 * @date 2022/11/15 14:32
 */
@Slf4j
@Component(value = "elasticSearchStrategy")
public class ElasticSearchStrategy implements SearchStrategy {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<ArticleSearchDTO> searchArticle(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }
        return search(queryBuilder(keyword));
    }

    /**
     * @param keyword 关键词
     * @return elasticsearch条件构造器
     */
    private NativeSearchQueryBuilder queryBuilder(String keyword) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //根据关键词搜索文章标题或文章内容
        boolQueryBuilder
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("articleTitle", keyword))
                        .should(QueryBuilders.matchQuery("articleContent", keyword)))
                .must(QueryBuilders.termQuery("isDelete", FALSE))
                .must(QueryBuilders.termQuery("status", PUBLIC.getStatus()));
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        return nativeSearchQueryBuilder;
    }

    /**
     * 文章搜索+关键词高亮
     *
     * @param nativeSearchQueryBuilder elasticsearch条件构造器
     * @return 搜索结果
     */
    public List<ArticleSearchDTO> search(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        //文章标题高亮
        HighlightBuilder.Field titleField = new HighlightBuilder.Field("articleTitle");
        titleField.preTags(PRE_TAG);
        titleField.postTags(POST_TAG);
        //文章内容高亮
        HighlightBuilder.Field contentField = new HighlightBuilder.Field("articleContent");
        contentField.preTags(PRE_TAG);
        contentField.postTags(POST_TAG);
        contentField.fragmentSize(200);
        nativeSearchQueryBuilder.withHighlightFields(titleField, contentField);
        try {
            SearchHits<ArticleSearchDTO> searchHits = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), ArticleSearchDTO.class);
            return searchHits.getSearchHits().stream().map(hit -> {
                ArticleSearchDTO article = hit.getContent();
                //获取文章标题高亮数据
                List<String> titleHighLightList = hit.getHighlightField("articleTitle");
                if (CollectionUtils.isNotEmpty(titleHighLightList)) {
                    article.setArticleTitle(titleHighLightList.get(0));
                }
                //获取文章内容高亮数据
                List<String> contentHighLightList = hit.getHighlightField("articleContent");
                if (CollectionUtils.isNotEmpty(contentHighLightList)) {
                    article.setArticleContent(contentHighLightList.get(contentHighLightList.size() - 1));
                }
                return article;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }
}
