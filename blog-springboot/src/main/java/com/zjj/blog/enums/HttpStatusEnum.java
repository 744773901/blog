package com.zjj.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接口状态码枚举
 *
 * @author 知白守黑
 * @date 2022/7/15 18:03
 */
@Getter
@AllArgsConstructor
public enum HttpStatusEnum {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    /**
     * 失败
     */
    FAIL(400, "操作失败"),
    /**
     * 未登录
     */
    NO_LOGIN(401, "用户未登录或登录过期"),
    /**
     * 用户名或密码错误
     */
    AUTHENTICATION_FAILED(402, "用户名或密码错误"),
    /**
     * 没有访问权限
     */
    NO_AUTHORIZATION(403, "没有访问权限"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "系统异常"),
    /**
     * 验证码错误
     */
    ERROR_CAPTCHA(501, "验证码错误"),
    /**
     * 验证码过期
     */
    INVALID_CAPTCHA(501, "验证码过期"),
    /**
     * 参数校验失败
     */
    VALID_ERROR(502, "参数格式不正确"),
    /**
     * 用户名已存在
     */
    USERNAME_EXISTED(503, "用户名已存在"),
    /**
     * 用户名不存在
     */
    USERNAME_NOT_EXIST(504, "用户名不存在"),
    /**
     * qq登录错误
     */
    QQ_LOGIN_ERROR(505, "qq登录错误"),
    /**
     * 微博登录错误
     */
    WEIBO_LOGIN_ERROR(506, "微博登录错误");

    private final Integer code;
    private final String message;
}
