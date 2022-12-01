package com.zjj.blog.security.domain;

import lombok.Data;

/**
 * 登录表单信息
 *
 * @author 知白守黑
 * @date 2022/7/15 23:20
 */
@Data
public class LoginBody {
    private String username;
    private String password;
    private String code;
    private String uuid;
}
