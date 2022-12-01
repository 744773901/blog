package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.*;
import com.zjj.blog.entity.Article;
import com.zjj.blog.entity.ArticleTag;
import com.zjj.blog.entity.Category;
import com.zjj.blog.entity.Tag;
import com.zjj.blog.enums.StatusEnum;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.ArticleMapper;
import com.zjj.blog.mapper.ArticleTagMapper;
import com.zjj.blog.mapper.CategoryMapper;
import com.zjj.blog.mapper.TagMapper;
import com.zjj.blog.service.ArticleService;
import com.zjj.blog.service.ArticleTagService;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.service.TagService;
import com.zjj.blog.strategy.context.SearchStrategyContext;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.CommonUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.utils.UserUtil;
import com.zjj.blog.vo.ArticleVO;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.DeleteVO;
import com.zjj.blog.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.FALSE;
import static com.zjj.blog.constant.RedisConst.*;
import static com.zjj.blog.enums.StatusEnum.PUBLIC;

/**
 * @author 知白守黑
 * @date 2022/7/21 22:45
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private TagService tagService;
    @Resource
    private ArticleTagService articleTagService;
    @Resource
    private HttpSession session;
    @Resource
    private SearchStrategyContext searchStrategyContext;

    @Override
    public PageResult<ArchiveDTO> listArchives() {
        Page<Article> page = new Page<>(PageUtil.getCurrent(), PageUtil.getSize());
        Page<Article> articlePage = articleMapper.selectPage(page, new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getCreateTime)
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .orderByDesc(Article::getCreateTime));
        List<ArchiveDTO> archiveList = BeanCopyUtil.copyList(articlePage.getRecords(), ArchiveDTO.class);
        return new PageResult<>(archiveList, ((int) articlePage.getTotal()));
    }

    @Override
    public ArticlePreviewListDTO listArticlesByCondition(ConditionVO condition) {
        // 查询文章
        List<ArticlePreviewDTO> articlePreviewDTOList = articleMapper.listArticlesByCondition(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        // 搜索条件对应名(标签或分类名)
        String name;
        if (Objects.nonNull(condition.getCategoryId())) {
            name = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                            .select(Category::getCategoryName)
                            .eq(Category::getId, condition.getCategoryId()))
                    .getCategoryName();
        } else {
            name = tagService.getOne(new LambdaQueryWrapper<Tag>()
                            .select(Tag::getTagName)
                            .eq(Tag::getId, condition.getTagId()))
                    .getTagName();
        }
        return ArticlePreviewListDTO.builder()
                .articlePreviewDTOList(articlePreviewDTOList)
                .name(name)
                .build();
    }

    @Override
    public List<ArticleSearchDTO> searchArticle(ConditionVO condition) {
        return searchStrategyContext.executeSearchArticle(condition.getKeyword());
    }

    @Override
    public ArticleDTO getArticleById(Integer id) {
        CompletableFuture<List<ArticleRecommendDTO>> recommendArticles = CompletableFuture.supplyAsync(() -> articleMapper.listArticleRecommend(id));
        CompletableFuture<List<ArticleRecommendDTO>> newestArticles = CompletableFuture.supplyAsync(() -> {
            List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                    .select(Article::getId, Article::getArticleCover, Article::getArticleTitle, Article::getCreateTime)
                    .eq(Article::getIsDelete, FALSE)
                    .eq(Article::getStatus, PUBLIC.getStatus())
                    .orderByDesc(Article::getCreateTime)
                    .orderByDesc(Article::getId)
                    .last("LIMIT 5"));
            return BeanCopyUtil.copyList(articles, ArticleRecommendDTO.class);
        });
        ArticleDTO article = articleMapper.selectArticleById(id);
        if (Objects.isNull(article)) {
            throw new BusinessException("文章不存在");
        }
        //更新文章浏览量
        updateArticleViewsCount(id);
        //查询上下篇文章
        Article lastArticle = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleCover, Article::getArticleTitle)
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .lt(Article::getId, id)
                .orderByDesc(Article::getId)
                .last("LIMIT 1"));
        Article nextArticle = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleCover, Article::getArticleTitle)
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .gt(Article::getId, id)
                .orderByAsc(Article::getId)
                .last("LIMIT 1"));
        article.setLastArticle(BeanCopyUtil.copyObject(lastArticle, ArticlePaginationDTO.class));
        article.setNextArticle(BeanCopyUtil.copyObject(nextArticle, ArticlePaginationDTO.class));
        Double score = redisService.zScore(ARTICLE_VIEWS_COUNT, id);
        if (Objects.nonNull(score)) {
            article.setViewsCount(score.intValue());
        }
        article.setLikeCount((Integer) redisService.hGet(ARTICLE_LIKE_COUNT, id.toString()));
        try {
            article.setRecommendArticleList(recommendArticles.get());
            article.setNewestArticleList(newestArticles.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return article;
    }

    /**
     * 更新文章浏览量
     *
     * @param id 文章id
     */
    private void updateArticleViewsCount(Integer id) {
        Set<Integer> articleSet = CommonUtil.toSet(Optional.ofNullable(session.getAttribute("articleSet")).orElseGet(HashSet::new), Integer.class);
        if (!articleSet.contains(id)) {
            articleSet.add(id);
            session.setAttribute("articleSet", articleSet);
            redisService.zIncr(ARTICLE_VIEWS_COUNT, id, 1D);
        }
    }

    @Override
    public List<ArticleHomeDTO> listArticles() {
        return articleMapper.selectArticleList(PageUtil.getLimitCurrent(), PageUtil.getSize());
    }

    @Override
    public PageResult<AdminArticleDTO> listAdminArticles(ConditionVO condition) {
        Integer count = articleMapper.queryArticleCount(condition);
        if (count == 0) {
            return new PageResult<>();
        }
        List<AdminArticleDTO> adminArticleList = articleMapper.listAdminArticles(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        Map<Object, Double> articleViewCount = redisService.zAllScore(ARTICLE_VIEWS_COUNT);
        Map<String, Object> articleLikeCount = redisService.hGetAll(ARTICLE_LIKE_COUNT);
        adminArticleList.forEach(item -> {
            Double viewsCount = articleViewCount.get(item.getId());
            if (Objects.nonNull(viewsCount)) {
                item.setViewCount(viewsCount.intValue());
            }
            item.setLikeCount((Integer) articleLikeCount.get(item.getId().toString()));
        });
        return new PageResult<>(adminArticleList, count);
    }

    @Override
    public ArticleVO getAdminArticleById(Integer id) {
        Article article = articleMapper.selectById(id);
        Category category = categoryMapper.selectById(article.getCategoryId());
        String categoryName = null;
        if (Objects.nonNull(category)) {
            categoryName = category.getCategoryName();
        }
        List<String> tagNameList = tagMapper.listTagNameByArticleId(id);
        ArticleVO articleVO = BeanCopyUtil.copyObject(article, ArticleVO.class);
        articleVO.setCategoryName(categoryName);
        articleVO.setTagNameList(tagNameList);
        return articleVO;
    }

    @Override
    public void saveOrUpdateArticle(ArticleVO articleVO) {
        Category category = saveArticleCategory(articleVO);
        Article article = BeanCopyUtil.copyObject(articleVO, Article.class);
        if (Objects.nonNull(category)) {
            article.setCategoryId(category.getId());
        }
        article.setUserId(UserUtil.getLoginUser().getUserInfoId());
        this.saveOrUpdate(article);
        saveArticleTag(articleVO, article.getId());
    }

    private void saveArticleTag(ArticleVO articleVO, Integer id) {
        // 编辑文章则删除文章所有标签
        if (Objects.nonNull(articleVO.getId())) {
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
                    .eq(ArticleTag::getArticleId, articleVO.getId()));
        }
        // 添加文章标签
        List<String> tagNameList = articleVO.getTagNameList();
        if (CollectionUtils.isNotEmpty(tagNameList)) {
            // 查询已存在的标签
            List<Tag> existTagList = tagService.list(new LambdaQueryWrapper<Tag>()
                    .in(Tag::getTagName, tagNameList));
            List<String> existTagNameList = existTagList.stream()
                    .map(Tag::getTagName)
                    .collect(Collectors.toList());
            List<Integer> existTagIdList = existTagList.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toList());
            // 对比新增不存在的标签
            tagNameList.removeAll(existTagNameList);
            if (CollectionUtils.isNotEmpty(tagNameList)) {
                List<Tag> tagList = tagNameList.stream().map(item -> Tag.builder()
                                .tagName(item)
                                .build())
                        .collect(Collectors.toList());
                tagService.saveBatch(tagList);
                List<Integer> tagIdList = tagList.stream()
                        .map(Tag::getId)
                        .collect(Collectors.toList());
                existTagIdList.addAll(tagIdList);
            }
            // 提取标签id绑定文章
            List<ArticleTag> articleTagList = existTagIdList.stream().map(item -> ArticleTag.builder()
                            .articleId(id)
                            .tagId(item)
                            .build())
                    .collect(Collectors.toList());
            articleTagService.saveBatch(articleTagList);
        }
    }

    private Category saveArticleCategory(ArticleVO articleVO) {
        //查询分类是否存在
        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getCategoryName, articleVO.getCategoryName()));
        if (Objects.isNull(category) && !articleVO.getStatus().equals(StatusEnum.DRAFT.getStatus())) {
            category = Category
                    .builder()
                    .categoryName(articleVO.getCategoryName())
                    .build();
            categoryMapper.insert(category);
        }
        return category;
    }

    @Override
    public void saveArticleLike(Integer articleId) {
        String articleLikeKey = USER_ARTICLE_LIKE + UserUtil.getLoginUser().getUserInfoId();
        if (redisService.sIsMember(articleLikeKey, articleId)) {
            redisService.sRemove(articleLikeKey, articleId);
            redisService.hDecr(ARTICLE_LIKE_COUNT, articleId.toString(), 1L);
        } else {
            redisService.sAdd(articleLikeKey, articleId);
            redisService.hIncr(ARTICLE_LIKE_COUNT, articleId.toString(), 1L);
        }
    }

    @Override
    public void updateArticleTop(ArticleTopDTO articleTop) {
        Article article = Article.builder()
                .id(articleTop.getId())
                .isTop(articleTop.getIsTop())
                .build();
        articleMapper.updateById(article);
    }

    @Override
    public void updateArticleLogicDelete(DeleteVO deleteVO) {
        List<Article> articleList = deleteVO.getIds().stream()
                .map(id -> Article.builder()
                        .id(id)
                        .isDelete(deleteVO.getIsDelete())
                        .build())
                .collect(Collectors.toList());
        this.updateBatchById(articleList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteArticle(List<Integer> ids) {
        //删除文章标签关联表对应数据
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
                .in(ArticleTag::getArticleId, ids));
        //删除文章
        articleMapper.deleteBatchIds(ids);
    }
}
