package com.zjj.blog.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjj.blog.dto.ArticleRankDTO;
import com.zjj.blog.dto.ArticleStatisticsDTO;
import com.zjj.blog.dto.UniqueViewDTO;
import com.zjj.blog.entity.Article;
import com.zjj.blog.mapper.ArticleMapper;
import com.zjj.blog.mapper.UniqueViewMapper;
import com.zjj.blog.service.BlogInfoService;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.service.UserAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.RedisConst.ARTICLE_VIEWS_COUNT;
import static com.zjj.blog.constant.RedisConst.VISITOR_AREA;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogInfoServiceImplTest {
    @Resource
    private BlogInfoService blogInfoService;
    @Resource
    private UniqueViewMapper uniqueViewMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private UserAuthService userAuthService;

    @Test
    void getWebsiteConfig() {
        System.out.println(blogInfoService.getWebsiteConfig());
    }

    @Test
    public void test(){
        DateTime startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -7));
        DateTime endTime = DateUtil.endOfDay(new Date());
        List<UniqueViewDTO> list = uniqueViewMapper.listUniqueView(startTime, endTime);
        list.forEach(System.out::println);
    }

    @Test
    public void test1(){
        Map<Object, Double> articleMap = redisService.zReverseRangeWithScore(ARTICLE_VIEWS_COUNT, 0, 4);
        List<Integer> ids = new ArrayList<>(articleMap.size());
        articleMap.forEach((k, v) -> ids.add(((Integer) k)));
        List<ArticleRankDTO> list = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .select(Article::getId, Article::getArticleTitle)
                        .in(Article::getId, ids))
                .stream().map(article ->
                        ArticleRankDTO.builder()
                                .articleTitle(article.getArticleTitle())
                                .viewsCount(articleMap.get(article.getId()).intValue())
                                .build()
                ).sorted(Comparator.comparing(ArticleRankDTO::getViewsCount).reversed())
                .collect(Collectors.toList());
        list.forEach(System.out::println);
    }

    @Test
    public void test2(){
        userAuthService.statisticUserArea();
        redisService.hIncr(VISITOR_AREA, "广东", 1L);
    }
}