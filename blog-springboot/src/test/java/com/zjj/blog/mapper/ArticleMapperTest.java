package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjj.blog.dto.ArticleHomeDTO;
import com.zjj.blog.dto.ArticleRecommendDTO;
import com.zjj.blog.dto.ArticleSearchDTO;
import com.zjj.blog.dto.TagDTO;
import com.zjj.blog.entity.Article;
import com.zjj.blog.entity.ArticleTag;
import com.zjj.blog.entity.Tag;
import com.zjj.blog.service.TagService;
import com.zjj.blog.vo.ConditionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.*;
import static com.zjj.blog.enums.StatusEnum.PUBLIC;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleMapperTest {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;
    @Resource
    private TagService tagService;

    @Test
    void queryArticleCount() {
        ConditionVO conditionVO = ConditionVO.builder()
                .isDelete(0)
                .categoryId(187)
                .keyword("测试")
                .type(1)
                .status(1)
                .tagId(29)
                .build();
        System.out.println(articleMapper.queryArticleCount(conditionVO));
    }

    @Test
    public void testListAdminArticles() {
        ConditionVO conditionVO = ConditionVO.builder()
                .isDelete(0)
                .tagId(29)
                .current(0L)
                .size(10L)
                .build();
        articleMapper.listAdminArticles(0L, 10L, conditionVO).forEach(System.out::println);
    }

    @Test
    public void testArticleTag() {
        List<Integer> ids = new ArrayList<>();
        ids.add(55);
        ids.add(56);
        articleTagMapper
                .selectList(new LambdaQueryWrapper<ArticleTag>()
                        .in(ArticleTag::getArticleId, ids))
                .forEach(System.out::println);
    }

    @Test
    public void testTagService() {

        List<TagDTO> tagList = tagService.listTagByOption(ConditionVO.builder()
                .keyword(null)
                .build()
        );
        tagList.forEach(System.out::println);
    }

    @Test
    void selectArticleList() {
        List<ArticleHomeDTO> list = articleMapper.selectArticleList(0L, 10L);
        list.forEach(System.out::println);
    }

    @Test
    public void testArticle() {
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleCover, Article::getArticleTitle)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getStatus, 1)
                .lt(Article::getId, 73)
                .orderByDesc(Article::getId)
                .last("LIMIT 1"));
        System.out.println(article);
        System.out.println("===============");
        Article article2 = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleCover, Article::getArticleTitle)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getStatus, 1)
                .gt(Article::getId, 73)
                .orderByDesc(Article::getId)
                .last("LIMIT 1"));
        System.out.println(article2);
        System.out.println("===============");
        List<ArticleRecommendDTO> recommendDTOList = articleMapper.listArticleRecommend(73);
        recommendDTOList.forEach(System.out::println);
        System.out.println("===============");
        List<Article> newestArticles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover, Article::getCreateTime)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getCreateTime)
                .orderByDesc(Article::getId)
                .last("limit 5"));
        newestArticles.forEach(System.out::println);
    }

    @Test
    public void testSearchArticle(){
        String keyword = "666";
        List<Article> articleList = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .like(Article::getArticleTitle, keyword)
                .or()
                .like(Article::getArticleContent, keyword));
        articleList.forEach(System.out::println);
        List<ArticleSearchDTO> articleSearchDTOList = articleList.stream().map(article -> {
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
                    .build();
        }).collect(Collectors.toList());
        articleSearchDTOList.forEach(System.out::println);
    }

    @Test
    @SuppressWarnings({"all"})
    public void testHighLight(){
        String text = "666666666666666666666666666666666666666666666666恭喜你成功运行博客，开启你的文章之旅吧。66666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666";
        text = "Java恭喜你成功运行博客，java成功开启你的文章之旅吧。";
        String keyword = "成功";
        //keyword = "java";
        System.out.println(text.toLowerCase());
        keyword = keyword.toLowerCase();
        int keyBeginIndex = text.toLowerCase().indexOf(keyword);
        if (keyBeginIndex != -1) {
            int preIndex = keyBeginIndex > 25 ? keyBeginIndex - 25 : 0;
            String prevText = text.substring(preIndex, keyBeginIndex);

            int keyEndIndex = keyBeginIndex + keyword.length();
            int followingLength = text.length() - keyEndIndex;
            int index = followingLength > 175 ? keyEndIndex + 175 : keyEndIndex + followingLength;
            String followingText = text.substring(keyBeginIndex, index);
            text = (prevText + followingText).toLowerCase().replaceAll(keyword, PRE_TAG + keyword + POST_TAG);
            System.out.println(text);
        }
    }

    public static void main(String[] args) {
        String text = "666666666666666666666666666666666666666666666666恭喜你成功运行博客，开启你的文章之旅吧。66666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666";
        text = "恭喜你成功运行博客，开启你的文章之旅吧。";
        String keyword = "成功";
        h1(text, keyword);
    }

    public static void h1(String text, String keyword) {
        int index = text.indexOf(keyword);
        if (index != -1) {
            int preIndex = index > 25 ? index - 25 : 0;
            String preText = text.substring(preIndex, index);

            int last = index + keyword.length();
            int postLength = text.length() - last;
            int postIndex = postLength > 5 ? last + 5 : last + postLength;
            String postText = text.substring(index, postIndex);
            text = (preText + postText).replaceAll(keyword, PRE_TAG + keyword + POST_TAG);
            System.out.println(text);
        }
    }
}