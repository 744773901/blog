package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.AdminBlogInfoDTO;
import com.zjj.blog.dto.BlogHomeInfoDTO;
import com.zjj.blog.enums.FilePathEnum;
import com.zjj.blog.service.BlogInfoService;
import com.zjj.blog.service.impl.WebSocketServiceImpl;
import com.zjj.blog.strategy.context.UploadStrategyContext;
import com.zjj.blog.vo.AboutVO;
import com.zjj.blog.vo.Result;
import com.zjj.blog.vo.VoiceVO;
import com.zjj.blog.vo.WebsiteConfigVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static com.zjj.blog.constant.OperateTypeConst.UPDATE;

/**
 * @author 知白守黑
 * @date 2022/8/23 21:59
 */
@Api(tags = "博客信息")
@RestController
public class BlogInfoController {
    @Resource
    private BlogInfoService blogInfoService;
    @Resource
    private UploadStrategyContext uploadStrategyContext;
    @Resource
    private WebSocketServiceImpl webSocketService;

    /**
     * 查看博客信息
     *
     * @return {@link Result<BlogHomeInfoDTO>} 博客信息
     */
    @ApiOperation(value = "查看博客信息")
    @GetMapping("/")
    public Result<BlogHomeInfoDTO> getBlogHomeInfo() {
        return Result.ok(blogInfoService.getBlogHomeInfo());
    }

    /**
     * 查询后台首页信息
     *
     * @return {@link AdminBlogInfoDTO}
     */
    @ApiOperation(value = "查询后台首页信息")
    @GetMapping("/admin")
    public Result<AdminBlogInfoDTO> getAdminBlogInfo() {
        return Result.ok(blogInfoService.getAdminBlogInfo());
    }

    /**
     * 上传访客信息
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "上传访客信息")
    @PostMapping("/report")
    public Result<?> report() {
        blogInfoService.report();
        return Result.ok();
    }

    /**
     * 获取网站配置
     *
     * @return {@link Result<WebsiteConfigVO>} 网站配置
     */
    @ApiOperation(value = "获取网站配置")
    @GetMapping("/admin/website/config")
    public Result<WebsiteConfigVO> getWebsiteConfig() {
        return Result.ok(blogInfoService.getWebsiteConfig());
    }

    /**
     * 上传网站配置图片
     *
     * @param file 文件
     * @return 图片访问地址
     */
    @ApiOperation(value = "上传网站配置图片")
    @PostMapping("/admin/config/images")
    public Result<?> uploadWebsiteConfigImage(MultipartFile file) {
        return Result.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.CONFIG.getPath()));
    }

    /**
     * 修改网站配置
     *
     * @param websiteConfigVO 网站配置
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "修改网站配置")
    @PutMapping("/admin/website/config")
    public Result<?> updateWebsiteConfig(@Validated @RequestBody WebsiteConfigVO websiteConfigVO) {
        blogInfoService.updateWebsiteConfig(websiteConfigVO);
        return Result.ok();
    }

    /**
     * 查询Redis中关于我内容
     *
     * @return 关于我内容
     */
    @ApiOperation(value = "查询Redis中关于我内容")
    @GetMapping("/about")
    public Result<String> getAbout() {
        return Result.ok(blogInfoService.getAbout());
    }

    /**
     * 修改关于我内容
     *
     * @param aboutVO 关于我内容
     * @return {@link Result<>}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "修改关于我内容")
    @PutMapping("/admin/about")
    public Result<?> updateAbout(@Validated @RequestBody AboutVO aboutVO) {
        blogInfoService.updateAbout(aboutVO);
        return Result.ok();
    }

    /**
     * 上传语音
     * @param voiceVO 上传语音信息
     * @return 语音地址
     */
    @ApiOperation(value = "上传语音")
    @PostMapping("/voice")
    public Result<?> sendVoice(VoiceVO voiceVO) {
        webSocketService.sendVoice(voiceVO);
        return Result.ok();
    }
}
