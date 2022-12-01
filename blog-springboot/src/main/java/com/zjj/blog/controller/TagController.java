package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.TagDTO;
import com.zjj.blog.service.TagService;
import com.zjj.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/10 22:10
 */
@Api(tags = "标签模块")
@RestController
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 查询标签列表
     *
     * @return {@link Result<TagDTO>} 标签列表
     */
    @ApiOperation(value = "查询标签列表")
    @GetMapping("/tags")
    public Result<PageResult<TagDTO>> listTags() {
        return Result.ok(tagService.listTags());
    }

    /**
     * 文章标签选项
     *
     * @return 文章标签列表
     */
    @ApiOperation(value = "文章标签选项")
    @GetMapping("/admin/tag/option")
    public Result<List<TagDTO>> listTagByOption(ConditionVO condition) {
        return Result.ok(tagService.listTagByOption(condition));
    }

    /**
     * 查询后台标签
     *
     * @param condition 条件
     * @return 分页文章标签列表
     */
    @ApiOperation(value = "文章标签选项")
    @GetMapping("/admin/tags")
    public Result<PageResult<AdminTagVO>> listAdminTag(ConditionVO condition) {
        return Result.ok(tagService.listAdminTag(condition));
    }

    /**
     * 新增或修改标签
     *
     * @param tagVO 标签
     * @return {@link Result}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "新增或修改标签")
    @PostMapping("/admin/tag")
    public Result<?> saveOrUpdateTag(@Validated @RequestBody TagVO tagVO) {
        tagService.saveOrUpdateTag(tagVO);
        return Result.ok();
    }

    /**
     * 删除标签
     *
     * @param ids 标签id
     * @return {@link Result}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "删除标签")
    @DeleteMapping("/admin/tag")
    public Result<?> deleteTag(@RequestBody List<Integer> ids) {
        tagService.deleteTag(ids);
        return Result.ok();
    }
}
