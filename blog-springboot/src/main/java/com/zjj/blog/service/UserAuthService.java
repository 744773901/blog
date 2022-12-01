package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.UserAreaDTO;
import com.zjj.blog.entity.UserAuth;
import com.zjj.blog.security.domain.LoginBody;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PasswordVO;
import com.zjj.blog.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户认证服务
 *
 * @author 知白守黑
 * @date 2022/7/15 19:14
 */
public interface UserAuthService extends IService<UserAuth> {

    /**
     * 登录
     *
     * @param loginBody 登录信息
     * @return {@link Map<>} 返回token和用户信息
     */
    Map<String, Object> login(LoginBody loginBody);

    /**
     * 获取后台登录验证码
     *
     * @return {@link Map<>}
     */
    Map<String, Object> getCode();

    void validateCaptcha(String code, String uuid);

    /**
     * 后台修改管理员密码
     *
     * @param passwordVO 密码
     */
    void updateAdminPassword(PasswordVO passwordVO);

    /**
     * 修改密码
     *
     * @param userVO 用户信息
     */
    void updatePassword(UserVO userVO);

    /**
     * 查询用户区域分布
     *
     * @param condition 条件
     * @return 用户区域分布列表
     */
    List<UserAreaDTO> listUserAreas(ConditionVO condition);

    /**
     * 定时任务 统计用户地区
     */
    void statisticUserArea();

    /**
     * 退出登录 删除redis用户信息
     */
    void logout();

    /**
     * 发送验证码
     *
     * @param username 用户名(邮箱)
     */
    void sendCode(String username);

    /**
     * 用户注册
     *
     * @param userVO 用户注册信息
     */
    void register(UserVO userVO);
}
