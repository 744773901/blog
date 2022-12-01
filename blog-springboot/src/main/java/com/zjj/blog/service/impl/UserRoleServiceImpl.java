package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.entity.UserRole;
import com.zjj.blog.mapper.UserRoleMapper;
import com.zjj.blog.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * @author 知白守黑
 * @date 2022/8/17 20:35
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
