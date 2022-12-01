package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.AdminFriendLinkDTO;
import com.zjj.blog.dto.FriendLinkDTO;
import com.zjj.blog.service.FriendLinkService;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.FriendLinkVO;
import com.zjj.blog.vo.PageResult;
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
 * @date 2022/8/24 16:54
 */
@Api(tags = "友链模块")
@RestController
public class FriendLinkController {

    @Resource
    private FriendLinkService friendLinkService;

    /**
     * 查询友链列表
     *
     * @return {@link Result<FriendLinkDTO>} 友链列表
     */
    @ApiOperation(value = "查询友链列表")
    @GetMapping("/links")
    public Result<List<FriendLinkDTO>> listFriendLinks() {
        return Result.ok(friendLinkService.listFriendLinks());
    }

    /**
     * 查询后台友链列表
     *
     * @param condition 条件
     * @return 友链列表
     */
    @ApiOperation(value = "查询后台友链列表")
    @GetMapping("/admin/links")
    public Result<PageResult<AdminFriendLinkDTO>> listAdminFriendLink(ConditionVO condition) {
        return Result.ok(friendLinkService.listAdminFriendLink(condition));
    }

    /**
     * 新增或修改友链
     *
     * @param friendLinkVO 友链信息
     * @return {@link Result<>}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "新增或修改友链")
    @PostMapping("/admin/links")
    public Result<?> saveOrUpdateFriendLink(@Validated @RequestBody FriendLinkVO friendLinkVO) {
        friendLinkService.saveOrUpdateFriendLink(friendLinkVO);
        return Result.ok();
    }

    /**
     * 删除友链
     *
     * @param ids 友链id列表
     * @return {@link Result<>}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "删除友链")
    @DeleteMapping("/admin/links")
    public Result<?> deleteFriendLink(@RequestBody List<Integer> ids) {
        friendLinkService.removeByIds(ids);
        return Result.ok();
    }
}
