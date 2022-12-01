package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.service.PageService;
import com.zjj.blog.vo.PageVO;
import com.zjj.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/24 15:00
 */
@Api(tags = "页面模块")
@RestController
public class PageController {

    @Resource
    private PageService pageService;

    /**
     * 查询所有页面
     *
     * @return 页面列表
     */
    @ApiOperation(value = "查询所有页面")
    @GetMapping("/admin/pages")
    public Result<List<PageVO>> listPages() {
        return Result.ok(pageService.listPages());
    }

    /**
     * 新增或修改页面
     *
     * @param pageVO 页面信息
     * @return {@link Result<>}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "新增或修改页面")
    @PostMapping("/admin/pages")
    public Result<?> saveOrUpdatePage(@Validated @RequestBody PageVO pageVO) {
        pageService.saveOrUpdatePage(pageVO);
        return Result.ok();
    }

    /**
     * 根据id删除页面
     *
     * @param id 页面id
     * @return {@link Result <>}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "删除页面")
    @DeleteMapping("/admin/pages/{id}")
    public Result<?> deletePage(@PathVariable("id") Integer id) {
        pageService.deletePage(id);
        return Result.ok();
    }
}
