package com.zjj.blog.controller;

import com.zjj.blog.annotation.AccessRateLimit;
import com.zjj.blog.dto.UserAreaDTO;
import com.zjj.blog.security.domain.LoginBody;
import com.zjj.blog.service.UserAuthService;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PasswordVO;
import com.zjj.blog.vo.Result;
import com.zjj.blog.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/14 18:14
 */
@Api(tags = "用户账号模块")
@RestController
public class UserAuthController {

    @Resource
    private UserAuthService userAuthService;

    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginBody loginBody) {
        return Result.ok(userAuthService.login(loginBody));
    }

    @ApiOperation(value = "用户注销")
    @PostMapping("/logout")
    public Result<?> logout() {
        userAuthService.logout();
        return Result.ok();
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@Validated @RequestBody UserVO userVO) {
        userAuthService.register(userVO);
        return Result.ok();
    }

    @ApiOperation(value = "获取验证码")
    @GetMapping("/captchaImage")
    public Result<?> getCode() {
        return Result.ok(userAuthService.getCode());
    }

    @AccessRateLimit(second = 60, counter = 1)
    @ApiOperation(value = "发送验证码")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataTypeClass = String.class)
    @GetMapping("/user/code")
    public Result<?> sendCode(String username) {
        userAuthService.sendCode(username);
        return Result.ok();
    }

    /**
     * 后台修改管理员密码
     *
     * @param passwordVO 密码
     * @return {@link Result}
     */
    @ApiOperation(value = "后台修改管理员密码")
    @PutMapping("/admin/user/password")
    public Result<?> updateAdminPassword(@Validated @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return Result.ok();
    }

    /**
     * 修改密码
     *
     * @param userVO 用户信息
     * @return {@link Result<>}
     */
    @ApiOperation(value = "修改密码")
    @PutMapping("/user/password")
    public Result<?> updatePassword(@Validated @RequestBody UserVO userVO) {
        userAuthService.updatePassword(userVO);
        return Result.ok();
    }

    /**
     * 查询用户区域分布
     *
     * @param condition 条件
     * @return 用户区域分布
     */
    @ApiOperation(value = "查询用户区域分布")
    @GetMapping("/admin/users/area")
    public Result<List<UserAreaDTO>> listUserAreas(ConditionVO condition) {
        return Result.ok(userAuthService.listUserAreas(condition));
    }
}
