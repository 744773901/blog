package com.zjj.blog.strategy.search;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjj.blog.dto.ArticleSearchDTO;
import com.zjj.blog.entity.Article;
import com.zjj.blog.mapper.ArticleMapper;
import com.zjj.blog.strategy.SearchStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.*;
import static com.zjj.blog.enums.StatusEnum.PUBLIC;

/**
 * @author 知白守黑
 * @date 2022/11/15 14:30
 */
@Component(value = "mySqlSearchStrategy")
public class MySqlSearchStrategy implements SearchStrategy {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public List<ArticleSearchDTO> searchArticle(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }
        List<Article> articleList = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .like(Article::getArticleTitle, keyword)
                .or()
                .like(Article::getArticleContent, keyword));
        //关键词高亮
        return articleList.stream().map(article -> {
            String articleContent = article.getArticleContent();
            //记录第一次出现的关键词的开始索引
            int keyBeginIndex = articleContent.indexOf(keyword);
            if (keyBeginIndex != -1) {
                //判断关键词的keyBeginIndex前面的文本长度 大于25则截取keyBeginIndex前25位的文本 否则从文本开始位置截取
                int preIndex = keyBeginIndex > 25 ? keyBeginIndex - 25 : 0;
                String preText = articleContent.substring(preIndex, keyBeginIndex);
                //记录第一次出现的关键词的结束索引
                int keyEndIndex = keyBeginIndex + keyword.length();
                //获取关键词的keyEndIndex之后的文本长度 大于175则截取keyEndIndex之后175位的文本 否则全部截取
                int followingLength = articleContent.length() - keyEndIndex;
                int index = followingLength > 175 ? keyEndIndex + 175 : keyEndIndex + followingLength;
                String followingText = articleContent.substring(keyBeginIndex, index);
                articleContent = (preText + followingText).replaceAll(keyword, PRE_TAG + keyword + POST_TAG);
            }
            String articleTitle = article.getArticleTitle().replaceAll(keyword, PRE_TAG + keyword + POST_TAG);
            return ArticleSearchDTO.builder()
                    .id(article.getId())
                    .articleTitle(articleTitle)
                    .articleContent(articleContent)
                    .isDelete(article.getIsDelete())
                    .status(article.getStatus())
                    .build();
        }).collect(Collectors.toList());
    }
}
