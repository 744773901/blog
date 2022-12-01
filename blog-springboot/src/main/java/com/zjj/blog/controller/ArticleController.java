package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.*;
import com.zjj.blog.service.ArticleService;
import com.zjj.blog.strategy.context.UploadStrategyContext;
import com.zjj.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;
import static com.zjj.blog.enums.FilePathEnum.ARTICLE;

/**
 * @author 知白守黑
 * @date 2022/7/21 22:56
 */
@Api(tags = "文章模块")
@RestController
public class ArticleController {
    @Resource
    private ArticleService articleService;
    @Resource
    private UploadStrategyContext uploadStrategyContext;

    /**
     * 查看文章归档
     *
     * @return {@link Result<ArchiveDTO>} 文章归档列表
     */
    @ApiOperation(value = "查看文章归档")
    @GetMapping("/articles/archives")
    public Result<PageResult<ArchiveDTO>> listArchives() {
        return Result.ok(articleService.listArchives());
    }

    /**
     * 根据id查看文章
     *
     * @param id 文章id
     * @return {@link Result<ArticleDTO>} 文章信息
     */
    @ApiOperation(value = "根据id查看文章")
    @GetMapping("/article/{id}")
    public Result<ArticleDTO> getArticleById(@PathVariable("id") Integer id) {
        return Result.ok(articleService.getArticleById(id));
    }

    /**
     * 根据条件查询文章
     *
     * @param condition 条件
     * @return {@link Result<ArticlePreviewListDTO>} 文章列表
     */
    @ApiOperation(value = "根据条件查询文章")
    @GetMapping("/articles/condition")
    public Result<ArticlePreviewListDTO> listArticlesByCondition(ConditionVO condition) {
        return Result.ok(articleService.listArticlesByCondition(condition));
    }

    /**
     * 查询首页文章
     *
     * @return {@link Result<ArticleHomeDTO>} 首页文章列表
     */
    @ApiOperation(value = "查询首页文章")
    @GetMapping("/articles")
    public Result<List<ArticleHomeDTO>> listArticles() {
        return Result.ok(articleService.listArticles());
    }

    /**
     * 搜索文章
     *
     * @param condition 条件
     * @return {@link ArticleSearchDTO}
     */
    @ApiOperation(value = "搜索文章")
    @GetMapping("/article/search")
    public Result<List<ArticleSearchDTO>> searchArticle(ConditionVO condition) {
        return Result.ok(articleService.searchArticle(condition));
    }

    /**
     * 查看后台文章
     *
     * @param condition 条件
     * @return {@link Result<>} 后台文章列表
     */
    @ApiOperation(value = "查看后台文章")
    @GetMapping("/admin/articles")
    public Result<PageResult<AdminArticleDTO>> listAdminArticles(ConditionVO condition) {
        return Result.ok(articleService.listAdminArticles(condition));
    }

    /**
     * 根据id查询文章
     *
     * @param id 文章id
     * @return 文章
     */
    @ApiOperation("根据id查询文章")
    @GetMapping("/admin/article/{id}")
    public Result<ArticleVO> getAdminArticleById(@PathVariable("id") Integer id) {
        return Result.ok(articleService.getAdminArticleById(id));
    }

    /**
     * 添加或修改文章
     *
     * @param articleVO 文章
     * @return {@link Result<>}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "添加或修改文章")
    @PostMapping("/admin/article")
    public Result<?> saveOrUpdateArticle(@Validated @RequestBody ArticleVO articleVO) {
        articleService.saveOrUpdateArticle(articleVO);
        return Result.ok();
    }

    /**
     * 恢复或删除文章
     *
     * @param deleteVO 逻辑删除
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "恢复或删除文章")
    @PutMapping("/admin/article")
    public Result<?> updateArticleLogicDelete(@Validated @RequestBody DeleteVO deleteVO) {
        articleService.updateArticleLogicDelete(deleteVO);
        return Result.ok();
    }

    /**
     * 物理删除文章
     *
     * @param ids 文章ID
     * @return {@link Result}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "物理删除文章")
    @DeleteMapping("/admin/article")
    public Result<?> deleteArticle(@RequestBody List<Integer> ids) {
        articleService.deleteArticle(ids);
        return Result.ok();
    }

    /**
     * 文章点赞
     *
     * @param articleId 文章id
     * @return {@link Result}
     */
    @ApiOperation(value = "点赞文章")
    @ApiImplicitParam(name = "articleId", value = "文章ID", required = true, dataTypeClass = Integer.class)
    @PostMapping("/article/like/{articleId}")
    public Result<?> saveArticleLike(@PathVariable("articleId") Integer articleId) {
        articleService.saveArticleLike(articleId);
        return Result.ok();
    }

    /**
     * 修改文章置顶状态
     *
     * @param articleTop 文章置顶{@link ArticleTopDTO}
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "修改文章置顶状态")
    @PutMapping("/admin/article/top")
    public Result<?> updateArticleTop(@Validated @RequestBody ArticleTopDTO articleTop) {
        articleService.updateArticleTop(articleTop);
        return Result.ok();
    }

    /**
     * 上传文章图片
     *
     * @param file 图片
     * @return {@link Result} 图片地址
     */
    @ApiOperation(value = "上传文章图片")
    @PostMapping("/admin/article/image")
    public Result<?> uploadArticleImage(MultipartFile file) {
        return Result.ok(uploadStrategyContext.executeUploadStrategy(file, ARTICLE.getPath()));
    }
}
