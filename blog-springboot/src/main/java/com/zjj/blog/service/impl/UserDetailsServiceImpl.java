package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjj.blog.entity.UserAuth;
import com.zjj.blog.entity.UserInfo;
import com.zjj.blog.mapper.RoleMapper;
import com.zjj.blog.mapper.UserAuthMapper;
import com.zjj.blog.mapper.UserInfoMapper;
import com.zjj.blog.security.domain.LoginUser;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.utils.IPUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.zjj.blog.constant.CommonConst.TIMEZONE_SHANGHAI;
import static com.zjj.blog.constant.RedisConst.*;

/**
 * @author 知白守黑
 * @date 2022/7/15 23:53
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private RedisService redisService;
    @Resource
    private UserAuthMapper userAuthMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        UserAuth userAuth = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getId, UserAuth::getUserInfoId, UserAuth::getUsername, UserAuth::getPassword, UserAuth::getLoginType)
                .eq(UserAuth::getUsername, username));
        if (Objects.isNull(userAuth)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return convertUserDetail(userAuth, request);
    }

    public LoginUser convertUserDetail(UserAuth userAuth, HttpServletRequest request) {
        //查询用户信息
        UserInfo userInfo = userInfoMapper.selectById(userAuth.getUserInfoId());
        List<String> roleList = roleMapper.listRolesByUserinfoId(userAuth.getUserInfoId());
        Set<Object> articleLikeSet = redisService.sMembers(USER_ARTICLE_LIKE + userInfo.getId());
        Set<Object> commentLikeSet = redisService.sMembers(USER_COMMENT_LIKE + userInfo.getId());
        Set<Object> talkLikeSet = redisService.sMembers(USER_TALK_LIKE + userInfo.getId());
        String ipAddress = IPUtil.getIpAddress(request);
        String ipSource = IPUtil.getIpSource(ipAddress);
        UserAgent userAgent = IPUtil.getUserAgent(request);
        userAuth.setIpAddress(ipAddress);
        userAuth.setIpSource(ipSource);
        userAuth.setLastLoginTime(LocalDateTime.now(ZoneId.of(TIMEZONE_SHANGHAI)));
        return LoginUser.builder()
                .id(userAuth.getId())
                .userInfoId(userInfo.getId())
                .username(userAuth.getUsername())
                .password(userAuth.getPassword())
                .loginType(userAuth.getLoginType())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .lastLoginTime(LocalDateTime.now(ZoneId.of(TIMEZONE_SHANGHAI)))
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .webSite(userInfo.getWebSite())
                .isDisable(userInfo.getIsDisable())
                .articleLikeSet(articleLikeSet)
                .commentLikeSet(commentLikeSet)
                .talkLikeSet(talkLikeSet)
                .roleList(roleList)
                .browser(userAgent.getBrowser().getName())
                .os(userAgent.getOperatingSystem().getName())
                .build();
    }
}
