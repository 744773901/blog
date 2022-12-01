package com.zjj.blog.security;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 知白守黑
 * @date 2022/10/21 23:14
 */
@Component
public class AccessDecisionManagerImpl implements AccessDecisionManager {

    /**
     *
     * @param authentication 当前登录用户，以获取当前用户权限信息
     * @param object {@link FilterInvocation} FilterInvocation对象，以获取request信息
     * @param configAttributes 当前请求鉴权规则 角色权限集合
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 拿到当前登录用户的角色权限集合
        List<String> permissions = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // 遍历当前请求资源接口的角色权限集合并与当前登录用户的角色权限集合进行匹配，匹配到则有访问权限，否则抛出异常
        for (ConfigAttribute configAttribute : configAttributes) {
            if (permissions.contains(configAttribute.getAttribute())) {
                return;
            }
        }
        throw new AccessDeniedException("没有操作权限");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
