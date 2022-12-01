package com.zjj.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjj.blog.dto.*;
import com.zjj.blog.entity.Article;
import com.zjj.blog.entity.WebsiteConfig;
import com.zjj.blog.mapper.*;
import com.zjj.blog.service.BlogInfoService;
import com.zjj.blog.service.PageService;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.service.UniqueViewService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.IPUtil;
import com.zjj.blog.vo.AboutVO;
import com.zjj.blog.vo.PageVO;
import com.zjj.blog.vo.WebsiteConfigVO;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.*;
import static com.zjj.blog.constant.RedisConst.*;
import static com.zjj.blog.enums.StatusEnum.PUBLIC;

/**
 * @author 知白守黑
 * @date 2022/8/23 22:04
 */
@Service
public class BlogInfoServiceImpl implements BlogInfoService {
    @Resource
    private RedisService redisService;
    @Resource
    private WebsiteConfigMapper websiteConfigMapper;
    @Resource
    private HttpServletRequest request;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private PageService pageService;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UniqueViewService uniqueViewService;

    @Override
    public BlogHomeInfoDTO getBlogHomeInfo() {
        int articleCount = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, PUBLIC.getStatus())
                .eq(Article::getIsDelete, FALSE))
                .intValue();
        int categoryCount = categoryMapper.selectCount(null).intValue();
        int tagCount = tagMapper.selectCount(null).intValue();
        Object blogViewCount = redisService.get(BLOG_VIEWS_COUNT);
        String viewCount = Optional.ofNullable(blogViewCount).orElse(0).toString();
        WebsiteConfigVO websiteConfig = this.getWebsiteConfig();
        List<PageVO> pageList = pageService.listPages();
        return BlogHomeInfoDTO.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .viewsCount(viewCount)
                .websiteConfig(websiteConfig)
                .pageList(pageList)
                .build();
    }

    @Override
    public AdminBlogInfoDTO getAdminBlogInfo() {
        //查询访问量
        Object count = redisService.get(BLOG_VIEWS_COUNT);
        Integer viewCount = Integer.valueOf(Optional.ofNullable(count).orElse(0).toString());
        //查询留言量
        int messageCount = messageMapper.selectCount(null).intValue();
        //查询用户量
        int userCount = userInfoMapper.selectCount(null).intValue();
        //查询文章量
        int articleCount = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, FALSE)).intValue();
        //查询分类及其文章数量
        List<CategoryDTO> categoryList = categoryMapper.listCategories();
        //查询标签量
        List<TagDTO> tagList = BeanCopyUtil.copyList(tagMapper.selectList(null), TagDTO.class);
        //查询文章统计
        List<ArticleStatisticsDTO> articleStatisticsList = articleMapper.listArticleStatistics();
        //查询一周用户量
        List<UniqueViewDTO> uniqueViewDTOList = uniqueViewService.listUniqueView();
        //查询浏览量前五的文章
        Map<Object, Double> articleMap = redisService.zReverseRangeWithScore(ARTICLE_VIEWS_COUNT, 0, 4);
        AdminBlogInfoDTO adminBlogInfoDTO = AdminBlogInfoDTO.builder()
                .viewsCount(viewCount)
                .messageCount(messageCount)
                .userCount(userCount)
                .articleCount(articleCount)
                .categoryDTOList(categoryList)
                .tagDTOList(tagList)
                .articleStatisticsList(articleStatisticsList)
                .uniqueViewDTOList(uniqueViewDTOList)
                .build();
        if (CollectionUtils.isNotEmpty(articleMap)) {
            List<ArticleRankDTO> articleRankDTOList = listArticleRank(articleMap);
            adminBlogInfoDTO.setArticleRankDTOList(articleRankDTOList);
        }
        return adminBlogInfoDTO;
    }

    /**
     * 查询浏览量前五的文章
     *
     * @param articleMap 文章浏览量排名(key=文章id,value=文章浏览量)
     * @return 浏览量前五的文章
     */
    private List<ArticleRankDTO> listArticleRank(Map<Object, Double> articleMap) {
        List<Integer> ids = new ArrayList<>(articleMap.size());
        articleMap.forEach((k,v) -> ids.add(((Integer) k)));
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .select(Article::getId, Article::getArticleTitle)
                        .in(Article::getId, ids))
                .stream().map(article -> ArticleRankDTO.builder()
                        .articleTitle(article.getArticleTitle())
                        .viewsCount(articleMap.get(article.getId()).intValue())
                        .build())
                .sorted(Comparator.comparing(ArticleRankDTO::getViewsCount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public WebsiteConfigVO getWebsiteConfig() {
        WebsiteConfigVO websiteConfigVO;
        Object websiteConfig = redisService.get(WEBSITE_CONFIG);
        if (Objects.nonNull(websiteConfig)) {
            websiteConfigVO = JSON.parseObject(websiteConfig.toString(), WebsiteConfigVO.class);
        } else {
            String config = websiteConfigMapper.selectById(DEFAULT_CONFIG_ID).getConfig();
            websiteConfigVO = JSON.parseObject(config, WebsiteConfigVO.class);
            redisService.set(WEBSITE_CONFIG, config);
        }
        return websiteConfigVO;
    }

    @Override
    public void updateWebsiteConfig(WebsiteConfigVO websiteConfigVO) {
        WebsiteConfig config = WebsiteConfig.builder()
                .id(DEFAULT_CONFIG_ID)
                .config(JSON.toJSONString(websiteConfigVO))
                .build();
        websiteConfigMapper.updateById(config);
        redisService.del(WEBSITE_CONFIG);
    }

    @Override
    public String getAbout() {
        Object about = redisService.get(ABOUT);
        return Objects.nonNull(about) ? about.toString() : "";
    }

    @Override
    public void updateAbout(AboutVO aboutVO) {
        redisService.set(ABOUT, aboutVO.getAboutContent());
    }

    @Override
    public void report() {
        String ipAddress = IPUtil.getIpAddress(request);
        UserAgent userAgent = IPUtil.getUserAgent(request);
        OperatingSystem os = userAgent.getOperatingSystem();
        Browser browser = userAgent.getBrowser();
        String uuid = ipAddress + os.getName() + browser.getName();
        String md5 = DigestUtils.md5DigestAsHex(uuid.getBytes());
        if (!redisService.sIsMember(UNIQUE_VISITOR, md5)) {
            String ipSource = IPUtil.getIpSource(ipAddress);
            if (StringUtils.isNotBlank(ipSource)) {
                ipSource = ipSource.substring(0, 2)
                        .replaceAll(PROVINCE, "")
                        .replaceAll(CITY, "");
                redisService.hIncr(VISITOR_AREA, ipSource, 1L);
            } else {
                redisService.hIncr(VISITOR_AREA, UNKNOWN, 1L);
            }
            // 访问量+1
            redisService.incr(BLOG_VIEWS_COUNT, 1);
            // 保存唯一标识
            redisService.sAdd(UNIQUE_VISITOR, md5);
        }
    }
}
