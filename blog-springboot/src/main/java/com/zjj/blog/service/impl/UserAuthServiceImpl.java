package com.zjj.blog.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import com.zjj.blog.config.SecurityProperties;
import com.zjj.blog.dto.EmailDTO;
import com.zjj.blog.dto.UserAreaDTO;
import com.zjj.blog.dto.UserInfoDTO;
import com.zjj.blog.entity.UserAuth;
import com.zjj.blog.entity.UserInfo;
import com.zjj.blog.entity.UserRole;
import com.zjj.blog.enums.HttpStatusEnum;
import com.zjj.blog.enums.LoginTypeEnum;
import com.zjj.blog.enums.RoleEnum;
import com.zjj.blog.enums.UserAreaEnum;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.UserAuthMapper;
import com.zjj.blog.mapper.UserInfoMapper;
import com.zjj.blog.mapper.UserRoleMapper;
import com.zjj.blog.security.domain.LoginBody;
import com.zjj.blog.security.domain.LoginUser;
import com.zjj.blog.service.BlogInfoService;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.service.UserAuthService;
import com.zjj.blog.service.UserInfoService;
import com.zjj.blog.utils.CommonUtil;
import com.zjj.blog.utils.TokenUtil;
import com.zjj.blog.utils.UserUtil;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PasswordVO;
import com.zjj.blog.vo.UserVO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.*;
import static com.zjj.blog.constant.RabbitMQConst.EMAIL_EXCHANGE;
import static com.zjj.blog.constant.RedisConst.*;

/**
 * 用户认证服务
 *
 * @author 知白守黑
 * @date 2022/7/15 19:21
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisService redisService;
    @Resource
    private UserAuthMapper userAuthMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private SecurityProperties securityProperties;
    @Resource
    private TokenUtil tokenUtil;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private BlogInfoService blogInfoService;

    @Override
    public Map<String, Object> login(LoginBody loginBody) {
        if (Objects.nonNull(loginBody.getUuid())) {
            validateCaptcha(loginBody.getCode(), loginBody.getUuid());
        }
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginBody.getUsername(), loginBody.getPassword()));
        if (Objects.isNull(authenticate)) {
            throw new BusinessException("用户名或密码错误");
        }
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String token = securityProperties.getTokenStartWith() + tokenUtil.generateToken(loginUser);
        userInfoService.updateUserInfo(loginUser);
        UserInfoDTO userInfo = userInfoService.cacheLoginUserInfo(loginUser);
        Map<String, Object> map = new HashMap<>(2);
        map.put("token", token);
        map.put("userInfo", userInfo);
        return map;
    }

    @Override
    public void logout() {
        redisService.del(LOGIN_USER_KEY + UserUtil.getLoginUser().getUsername());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserVO userVO) {
        String code = redisService.get(CODE_KEY + userVO.getUsername()).toString();
        if (!userVO.getCode().equals(code)) {
            throw new BusinessException("验证码错误");
        }
        if (isExistUser(userVO.getUsername())) {
            throw new BusinessException("邮箱已被注册");
        }
        UserInfo userInfo = UserInfo.builder()
                .email(userVO.getUsername())
                .nickname(DEFAULT_NICKNAME + IdWorker.getId())
                .avatar(blogInfoService.getWebsiteConfig().getUserAvatar())
                .build();
        userInfoMapper.insert(userInfo);
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRole);
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .username(userVO.getUsername())
                .password(BCrypt.hashpw(userVO.getPassword(), BCrypt.gensalt()))
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        userAuthMapper.insert(userAuth);
    }

    private Boolean isExistUser(String username) {
        //查询用户名是否存在
        UserAuth user = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername)
                .eq(UserAuth::getUsername, username));
        return Objects.nonNull(user);
    }

    @Override
    public Map<String, Object> getCode() {
        Captcha captcha = new ArithmeticCaptcha(111, 36);
        captcha.setLen(2);
        String uuid = IdUtil.simpleUUID();
        String captchaValue = captcha.text();
        redisService.set(uuid, captchaValue, 120);
        Map<String, Object> map = new HashMap<>();
        map.put("img", captcha.toBase64());
        map.put("uuid", uuid);
        return map;
    }

    @Override
    public void sendCode(String username) {
        if (StringUtils.isBlank(username)) {
            throw new BusinessException("邮箱不能为空");
        }
        if (!CommonUtil.isEmail(username)) {
            throw new BusinessException("邮箱格式不合法");
        }
        String code = CommonUtil.randomCode();
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("验证码")
                .content("您的验证码为 " + code + " 有效期15分钟，请不要告诉他人哦！")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        redisService.set(CODE_KEY + username, code, CODE_EXPIRE_TIME);
    }

    @Override
    public void validateCaptcha(String code, String uuid) {
        String captchaValue = Optional
                .ofNullable(redisService.get(uuid))
                .orElseThrow(() -> new BusinessException(HttpStatusEnum.INVALID_CAPTCHA))
                .toString();
        redisService.del(uuid);
        if (!code.equalsIgnoreCase(captchaValue)) {
            throw new BusinessException(HttpStatusEnum.ERROR_CAPTCHA);
        }
    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        //获取登录用户账号ID
        Integer userId = UserUtil.getLoginUser().getId();
        //根据ID查询用户账号密码
        UserAuth user = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getId, userId));
        //检验并修改密码
        if (Objects.nonNull(user) && BCrypt.checkpw(passwordVO.getOldPassword(), user.getPassword())) {
            UserAuth userAuth = UserAuth.builder()
                    .id(userId)
                    .password(BCrypt.hashpw(passwordVO.getNewPassword(), BCrypt.gensalt()))
                    .build();
            userAuthMapper.updateById(userAuth);
        } else {
            throw new BusinessException("旧密码不正确");
        }
    }

    @Override
    public void updatePassword(UserVO userVO) {
        String code = redisService.get(CODE_KEY + userVO.getUsername()).toString();
        if (!userVO.getCode().equals(code)) {
            throw new BusinessException("验证码错误");
        }
        if (!isExistUser(userVO.getUsername())) {
            throw new BusinessException("用户不存在!");
        }
        userAuthMapper.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword, BCrypt.hashpw(userVO.getPassword(), BCrypt.gensalt()))
                .eq(UserAuth::getUsername, userVO.getUsername()));
    }

    @Override
    public List<UserAreaDTO> listUserAreas(ConditionVO condition) {
        List<UserAreaDTO> userAreaList = new ArrayList<>();
        UserAreaEnum userAreaEnum = Objects.requireNonNull(UserAreaEnum.getUserAreaType(condition.getType()));
        switch (userAreaEnum) {
            case USER:
                Object userArea = redisService.get(USER_AREA);
                if (userArea != null) {
                    userAreaList = JSON.parseArray(userArea.toString(), UserAreaDTO.class);
                }
                break;
            case VISITOR:
                Map<String, Object> map = redisService.hGetAll(VISITOR_AREA);
                if (map != null) {
                    userAreaList = map.entrySet().stream()
                            .map(item -> UserAreaDTO.builder()
                                    .name(item.getKey())
                                    .value(Long.valueOf(item.getValue().toString()))
                                    .build())
                            .collect(Collectors.toList());
                }
                break;
            default:
                break;
        }
        return userAreaList;
    }

    /**
     * 统计用户地区
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Override
    public void statisticUserArea() {
        Map<String, Long> map = userAuthMapper.selectList(new LambdaQueryWrapper<UserAuth>()
                        .select(UserAuth::getIpSource))
                .stream()
                .map(item -> {
                    if (StringUtils.isNotBlank(item.getIpSource())) {
                        return item.getIpSource().substring(0, 2)
                                .replaceAll(PROVINCE, "")
                                .replaceAll(CITY, "");
                    }
                    return UNKNOWN;
                }).collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        List<UserAreaDTO> userAreaList = map.entrySet().stream()
                .map(item -> UserAreaDTO.builder()
                        .name(item.getKey())
                        .value(item.getValue())
                        .build())
                .collect(Collectors.toList());
        redisService.set(USER_AREA, userAreaList);
    }
}
