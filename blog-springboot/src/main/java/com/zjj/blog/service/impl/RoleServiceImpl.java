package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.constant.CommonConst;
import com.zjj.blog.dto.RoleDTO;
import com.zjj.blog.dto.UserRoleDTO;
import com.zjj.blog.entity.Role;
import com.zjj.blog.entity.RoleMenu;
import com.zjj.blog.entity.RoleResource;
import com.zjj.blog.entity.UserRole;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.RoleMapper;
import com.zjj.blog.mapper.UserRoleMapper;
import com.zjj.blog.security.FilterInvocationSecurityMetadataSourceImpl;
import com.zjj.blog.service.RoleMenuService;
import com.zjj.blog.service.RoleResourceService;
import com.zjj.blog.service.RoleService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.RoleVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 知白守黑
 * @date 2022/8/17 20:53
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMenuService roleMenuService;
    @Resource
    private RoleResourceService roleResourceService;
    @Resource
    private FilterInvocationSecurityMetadataSourceImpl filterInvocationSecurityMetadataSource;

    @Override
    public List<UserRoleDTO> listUserRoles() {
        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .select(Role::getId, Role::getRoleName));
        return BeanCopyUtil.copyList(roles, UserRoleDTO.class);
    }

    @Override
    public PageResult<RoleDTO> listRoles(ConditionVO condition) {
        int count = roleMapper.selectCount(new LambdaQueryWrapper<Role>()
                        .like(StringUtils.isNotBlank(condition.getKeyword()), Role::getRoleName, condition.getKeyword()))
                .intValue();
        List<RoleDTO> roleList = roleMapper.listRoles(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        return new PageResult<>(roleList, count);
    }

    @Override
    public void deleteRoles(List<Integer> ids) {
        int count = userRoleMapper.selectCount(new LambdaQueryWrapper<UserRole>()
                .in(UserRole::getRoleId, ids)).intValue();
        if (count > 0) {
            throw new BusinessException("该角色下有关联用户");
        }
        roleMapper.deleteBatchIds(ids);
    }

    @Override
    public void saveOrUpdateRole(RoleVO roleVO) {
        Role existRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .select(Role::getId)
                .eq(Role::getId, roleVO.getId()));
        if (Objects.nonNull(existRole) && !existRole.getId().equals(roleVO.getId())) {
            throw new BusinessException("角色已存在");
        }
        Role role = Role.builder()
                .id(roleVO.getId())
                .roleName(roleVO.getRoleName())
                .roleLabel(roleVO.getRoleLabel())
                .isDisable(CommonConst.FALSE)
                .build();
        this.saveOrUpdate(role);
        if (Objects.nonNull(roleVO.getMenuIdList())) {
            if (Objects.nonNull(roleVO.getId())) {
                roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleVO.getId()));
            }
            List<RoleMenu> roleMenuList = roleVO.getMenuIdList().stream()
                    .map(id -> RoleMenu.builder()
                            .roleId(roleVO.getId())
                            .menuId(id)
                            .build())
                    .collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenuList);
        }
        if (Objects.nonNull(roleVO.getResourceIdList())) {
            if (Objects.nonNull(roleVO.getId())) {
                roleResourceService.remove(new LambdaQueryWrapper<RoleResource>()
                        .eq(RoleResource::getRoleId, roleVO.getId()));
            }
            List<RoleResource> roleResourceList = roleVO.getResourceIdList().stream()
                    .map(id -> RoleResource.builder()
                            .roleId(role.getId())
                            .resourceId(id)
                            .build())
                    .collect(Collectors.toList());
            roleResourceService.saveBatch(roleResourceList);
            //重新加载角色资源
            filterInvocationSecurityMetadataSource.clearDataSource();
        }
    }
}
