package com.zjj.blog.utils;

import com.zjj.blog.security.domain.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author 知白守黑
 * @date 2022/7/15 21:54
 */
//@Component
public class UserUtil {

    public static LoginUser getLoginUser() {
        return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}