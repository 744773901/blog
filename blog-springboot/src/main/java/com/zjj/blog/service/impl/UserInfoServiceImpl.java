package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.AdminUserDTO;
import com.zjj.blog.dto.UserInfoDTO;
import com.zjj.blog.dto.UserOnlineDTO;
import com.zjj.blog.entity.UserAuth;
import com.zjj.blog.entity.UserInfo;
import com.zjj.blog.entity.UserRole;
import com.zjj.blog.enums.FilePathEnum;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.UserAuthMapper;
import com.zjj.blog.mapper.UserInfoMapper;
import com.zjj.blog.security.domain.LoginUser;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.service.UserInfoService;
import com.zjj.blog.service.UserRoleService;
import com.zjj.blog.strategy.context.UploadStrategyContext;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.utils.UserUtil;
import com.zjj.blog.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.RedisConst.CODE_KEY;
import static com.zjj.blog.constant.RedisConst.LOGIN_USER_KEY;

/**
 * @author 知白守黑
 * @date 2022/7/28 20:08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserAuthMapper userAuthMapper;
    @Resource
    private UploadStrategyContext uploadStrategyContext;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RedisService redisService;

    @Override
    public UserInfoDTO getUserInfo() {
        LoginUser loginUser = UserUtil.getLoginUser();
        UserInfoDTO userInfo = userInfoMapper.getUserInfo(loginUser.getUserInfoId());
        userInfo.setRoleList(loginUser.getRoleList());
        return userInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserInfo(UserInfoVO userInfoVO) {
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtil.getLoginUser().getUserInfoId())
                .nickname(userInfoVO.getNickname())
                .intro(userInfoVO.getIntro())
                .webSite(userInfoVO.getWebSite())
                .build();
        userInfoMapper.updateById(userInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUserEmail(EmailVO emailVO) {
        String code = redisService.get(CODE_KEY + emailVO.getEmail()).toString();
        if (!emailVO.getCode().equals(code)) {
            throw new BusinessException("验证码错误");
        }
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtil.getLoginUser().getUserInfoId())
                .email(emailVO.getEmail())
                .build();
        userInfoMapper.updateById(userInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserInfo(LoginUser loginUser) {
        UserAuth userAuth = UserAuth.builder()
                .id(loginUser.getId())
                .ipAddress(loginUser.getIpAddress())
                .ipSource(loginUser.getIpSource())
                .lastLoginTime(loginUser.getLastLoginTime())
                .build();
        userAuthMapper.updateById(userAuth);
    }

    @Override
    public String updateUserAvatar(MultipartFile file) {
        String avatar = uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.AVATAR.getPath());
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtil.getLoginUser().getUserInfoId())
                .avatar(avatar)
                .build();
        userInfoMapper.updateById(userInfo);
        return avatar;
    }

    @Override
    public PageResult<AdminUserDTO> listAdminUser(ConditionVO condition) {
        Integer count = userAuthMapper.countUser(condition);
        if (count == 0) {
            return new PageResult<>();
        }
        List<AdminUserDTO> adminUserList = userAuthMapper.listAdminUser(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        return new PageResult<>(adminUserList, count);
    }

    @Override
    public void updateUserDisable(UserDisableVO userDisableVO) {
        userInfoMapper.updateById(UserInfo.builder()
                .id(userDisableVO.getId())
                .isDisable(userDisableVO.getIsDisable())
                .build());
    }

    @Override
    public void updateUserRole(UserRoleVO userRoleVO) {
        //更新用户昵称
        userInfoMapper.updateById(UserInfo.builder()
                .id(userRoleVO.getUserInfoId())
                .nickname(userRoleVO.getNickname())
                .build());
        userRoleService.remove(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userRoleVO.getUserInfoId()));
        List<UserRole> userRoleList = userRoleVO.getRoleIdList().stream().map(roleId -> UserRole.builder()
                        .roleId(roleId)
                        .userId(userRoleVO.getUserInfoId())
                        .build())
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoleList);
    }

    public UserInfoDTO cacheLoginUserInfo(LoginUser loginUser) {
        String loginKey = LOGIN_USER_KEY + loginUser.getUsername();
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(loginUser.getUserInfoId())
                .email(loginUser.getEmail())
                .loginType(loginUser.getLoginType())
                .username(loginUser.getUsername())
                .nickname(loginUser.getNickname())
                .roleList(loginUser.getRoleList())
                .avatar(loginUser.getAvatar())
                .intro(loginUser.getIntro())
                .webSite(loginUser.getWebSite())
                .articleLikeSet(loginUser.getArticleLikeSet())
                .commentLikeSet(loginUser.getCommentLikeSet())
                .talkLikeSet(loginUser.getTalkLikeSet())
                .ipAddress(loginUser.getIpAddress())
                .ipSource(loginUser.getIpSource())
                .lastLoginTime(loginUser.getLastLoginTime())
                .build();
        // 缓存登录用户信息 过期时间单位/秒
        redisService.set(loginKey, userInfo, 3600 * 4);
        return userInfo;
    }

    @Override
    public PageResult<UserOnlineDTO> listOnlineUser(ConditionVO condition) {
        List<UserOnlineDTO> userOnlineList = new ArrayList<>();
        redisService.keys(LOGIN_USER_KEY + "*").forEach(key -> {
            UserOnlineDTO userOnlineDTO = BeanCopyUtil.copyObject(redisService.get(key), UserOnlineDTO.class);
            userOnlineList.add(userOnlineDTO);
        });
        List<UserOnlineDTO> userList = userOnlineList.stream()
                .filter(item -> StringUtils.isBlank(condition.getKeyword()) || item.getNickname().contains(condition.getKeyword()))
                .sorted(Comparator.comparing(UserOnlineDTO::getLastLoginTime).reversed())
                .collect(Collectors.toList());
        List<UserOnlineDTO> records = userList.stream()
                .skip(PageUtil.getLimitCurrent())
                .limit(PageUtil.getSize())
                .collect(Collectors.toList());
        return new PageResult<>(records, userList.size());
    }

    @Override
    public void offlineUser(Integer id) {
        //TODO 下线在线用户处理
        UserAuth userAuth = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername)
                .eq(UserAuth::getUserInfoId, id));
        redisService.del(LOGIN_USER_KEY + userAuth.getUsername());
    }
}
