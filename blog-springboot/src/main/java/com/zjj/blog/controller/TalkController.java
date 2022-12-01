package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.AdminTalkDTO;
import com.zjj.blog.dto.TalkDTO;
import com.zjj.blog.enums.FilePathEnum;
import com.zjj.blog.service.TalkService;
import com.zjj.blog.strategy.context.UploadStrategyContext;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.Result;
import com.zjj.blog.vo.TalkVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/19 20:23
 */
@Api(tags = "说说模块")
@RestController
public class TalkController {
    @Resource
    private TalkService talkService;
    @Resource
    private UploadStrategyContext uploadStrategyContext;

    /**
     * 上传说说图片
     *
     * @param file 图片
     * @return 图片地址
     */
    @ApiOperation(value = "上传说说图片")
    @PostMapping("/admin/talk/images")
    public Result<?> uploadTalkImages(MultipartFile file) {
        return Result.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.TALK.getPath()));
    }

    /**
     * 发布或修改说说
     *
     * @param talkVO 说说信息
     * @return {@link Result}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "发布或修改说说")
    @PostMapping("/admin/talk")
    public Result<?> saveOrUpdateTalk(@Validated @RequestBody TalkVO talkVO) {
        talkService.saveOrUpdateTalk(talkVO);
        return Result.ok();
    }

    /**
     * 查询首页说说
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "查询首页说说")
    @GetMapping("/home/talks")
    public Result<List<String>> listHomeTalks() {
        return Result.ok(talkService.listHomeTalks());
    }

    /**
     * 查看说说列表
     *
     * @return {@link Result<TalkDTO>}
     */
    @ApiOperation(value = "查看说说列表")
    @GetMapping("/talks")
    public Result<PageResult<TalkDTO>> listTalks() {
        return Result.ok(talkService.listTalks());
    }

    /**
     * 点赞说说
     *
     * @param talkId 说说id
     * @return {@link Result<>}
     */
    @ApiOperation(value = "点赞说说")
    @PostMapping("/talk/{talkId}/like")
    public Result<?> saveTalkLike(@PathVariable("talkId") Integer talkId) {
        talkService.saveTalkLike(talkId);
        return Result.ok();
    }

    /**
     * 根据id查看说说
     *
     * @param talkId 说说id
     * @return {@link Result<TalkDTO>}
     */
    @ApiOperation(value = "根据id查看说说")
    @GetMapping("/talk/{talkId}")
    public Result<TalkDTO> getTalkById(@PathVariable("talkId") Integer talkId) {
        return Result.ok(talkService.getTalkById(talkId));
    }

    /**
     * 查询后台说说
     *
     * @param condition 条件
     * @return 说说列表
     */
    @ApiOperation(value = "查询后台说说")
    @GetMapping("/admin/talks")
    public Result<PageResult<AdminTalkDTO>> listAdminTalk(ConditionVO condition) {
        return Result.ok(talkService.listAdminTalk(condition));
    }

    /**
     * 根据id查询后台说说
     *
     * @param id 说说id
     * @return 说说
     */
    @ApiOperation(value = "根据id查询后台说说")
    @GetMapping("/admin/talk/{id}")
    public Result<AdminTalkDTO> getAdminTalkById(@PathVariable("id") Integer id) {
        return Result.ok(talkService.getAdminTalkById(id));
    }

    /**
     * 根据id删除说说
     *
     * @param id 说说id
     * @return {@link Result}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "根据id删除说说")
    @DeleteMapping("/admin/talk/{id}")
    public Result<?> deleteTalkById(@PathVariable("id") Integer id) {
        talkService.removeById(id);
        return Result.ok();
    }
}
