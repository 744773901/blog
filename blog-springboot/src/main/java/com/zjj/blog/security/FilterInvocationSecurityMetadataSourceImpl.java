package com.zjj.blog.security;

import com.zjj.blog.dto.RoleResourceDTO;
import com.zjj.blog.mapper.RoleMapper;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/10/21 22:01
 */
@Component
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {

    @Resource
    private RoleMapper roleMapper;
    /**
     * 角色资源权限集合
     */
    private static List<RoleResourceDTO> roleResourceList;

    //初始化Bean时调用
    @PostConstruct
    public void loadDataSource() {
        roleResourceList = roleMapper.listRoleResource();
    }

    public void clearDataSource() {
        roleResourceList = null;
    }

    /**
     *
     * @param object {@link FilterInvocation} 对请求参数的封装
     * @return 角色权限集合
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (CollectionUtils.isEmpty(roleResourceList)) {
            loadDataSource();
        }
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        // 获取请求uri和method
        String uri = request.getRequestURI();
        String method = request.getMethod();
        AntPathMatcher matcher = new AntPathMatcher();
        // 遍历角色资源权限集合
        for (RoleResourceDTO roleResource : roleResourceList) {
            // 匹配该请求的接口路径
            if (method.equals(roleResource.getRequestMethod()) && matcher.match(uri, roleResource.getUrl())) {
                // 获取匹配接口的角色信息，找到该请求所需的角色资源权限
                List<String> roleList = roleResource.getRoleList();
                // 返回角色权限集合，交给访问决策管理器AccessDecisionManager进行授权管理，如果角色信息为空，则禁止访问
                if (roleList.isEmpty()) {
                    return SecurityConfig.createList("disable");
                }
                return SecurityConfig.createList(roleList.toArray(new String[]{}));
            }
        }
        // 执行到这里就代表该请求无需授权即可访问，返回空
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
