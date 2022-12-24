package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.AdminUserDTO;
import com.zjj.blog.dto.UserInfoDTO;
import com.zjj.blog.dto.UserOnlineDTO;
import com.zjj.blog.service.UserInfoService;
import com.zjj.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static com.zjj.blog.constant.OperateTypeConst.UPDATE;

/**
 * @author 知白守黑
 * @date 2022/7/28 19:46
 */
@Api(tags = "用户信息模块")
@RestController
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation(value = "更新用户头像")
    @ApiImplicitParam(name = "file", value = "用户头像", required = true, dataTypeClass = MultipartFile.class)
    @PostMapping("/user/avatar")
    public Result<?> updateUserAvatar(MultipartFile file) {
        return Result.ok(userInfoService.updateUserAvatar(file));
    }

    /**
     * 修改用户信息
     *
     * @param userInfoVO 用户信息
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "修改用户信息")
    @PutMapping("/user/info")
    public Result<?> updateUserInfo(@Validated @RequestBody UserInfoVO userInfoVO) {
        userInfoService.updateUserInfo(userInfoVO);
        return Result.ok();
    }

    /**
     * 绑定用户邮箱
     *
     * @param emailVO 绑定邮箱
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "绑定用户邮箱")
    @PostMapping("/user/email")
    public Result<?> saveUserEmail(@Validated @RequestBody EmailVO emailVO) {
        userInfoService.saveUserEmail(emailVO);
        return Result.ok();
    }

    /**
     * 查询用户信息
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "查询用户信息")
    @GetMapping("/user/info")
    public Result<UserInfoDTO> getUserInfo() {
        return Result.ok(userInfoService.getUserInfo());
    }

    /**
     * 查询后台用户
     *
     * @param condition 条件
     * @return {@link Result}
     */
    @ApiOperation(value = "查询后台用户")
    @GetMapping("/admin/users")
    public Result<PageResult<AdminUserDTO>> listAdminUser(ConditionVO condition) {
        return Result.ok(userInfoService.listAdminUser(condition));
    }

    /**
     * 修改用户禁用状态
     *
     * @param userDisableVO 用户禁用信息
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "修改用户禁用状态")
    @PutMapping("/admin/user/disable")
    public Result<?> updateUserDisable(@Validated @RequestBody UserDisableVO userDisableVO) {
        userInfoService.updateUserDisable(userDisableVO);
        return Result.ok();
    }

    /**
     * 修改用户角色
     *
     * @param userRoleVO 用户角色信息
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "修改用户角色")
    @PutMapping("/admin/user/role")
    public Result<?> updateUserRole(@Validated @RequestBody UserRoleVO userRoleVO) {
        userInfoService.updateUserRole(userRoleVO);
        return Result.ok();
    }

    /**
     * 查询在线用户
     *
     * @param condition 条件
     * @return {@link Result}
     */
    @ApiOperation(value = "查询在线用户")
    @GetMapping("/admin/users/online")
    public Result<PageResult<UserOnlineDTO>> listOnlineUser(ConditionVO condition) {
        return Result.ok(userInfoService.listOnlineUser(condition));
    }

    /**
     * 强制下线用户
     *
     * @param id 用户信息ID
     * @return {@link Result}
     */
    @ApiOperation(value = "强制退出用户")
    @DeleteMapping("/admin/user/{id}/offline")
    public Result<?> offlineUser(@PathVariable("id") Integer id) {
        userInfoService.offlineUser(id);
        return Result.ok();
    }
}
