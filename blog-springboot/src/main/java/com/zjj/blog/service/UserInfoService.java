package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.AdminUserDTO;
import com.zjj.blog.dto.UserInfoDTO;
import com.zjj.blog.dto.UserOnlineDTO;
import com.zjj.blog.entity.UserInfo;
import com.zjj.blog.security.domain.LoginUser;
import com.zjj.blog.vo.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 知白守黑
 * @date 2022/7/28 20:07
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 查询用户信息
     *
     * @return 用户信息
     */
    UserInfoDTO getUserInfo();

    /**
     * 修改用户信息
     *
     * @param userInfoVO 用户信息
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 登录时更新用户信息
     */
    void updateUserInfo(LoginUser loginUser);

    /**
     * 修改用户头像
     *
     * @param file 头像
     * @return 头像地址
     */
    String updateUserAvatar(MultipartFile file);

    /**
     * 查询后台用户
     *
     * @param condition 条件
     * @return 后台用户列表
     */
    PageResult<AdminUserDTO> listAdminUser(ConditionVO condition);

    /**
     * 修改用户禁用状态
     *
     * @param userDisableVO 用户禁用信息
     */
    void updateUserDisable(UserDisableVO userDisableVO);

    /**
     * 修改用户角色
     *
     * @param userRoleVO 用户角色信息
     */
    void updateUserRole(UserRoleVO userRoleVO);

    /**
     * 缓存并返回登录用户信息
     *
     * @param loginUser 登录用户
     */
    UserInfoDTO cacheLoginUserInfo(LoginUser loginUser);

    /**
     * 查询在线用户
     *
     * @param condition 条件
     * @return 在线用户信息
     */
    PageResult<UserOnlineDTO> listOnlineUser(ConditionVO condition);

    /**
     * 强制下线用户
     *
     * @param id 用户信息ID
     */
    void offlineUser(Integer id);

    /**
     * 绑定用户邮箱
     *
     * @param emailVO 绑定邮箱
     */
    void saveUserEmail(EmailVO emailVO);
}
