package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.RoleDTO;
import com.zjj.blog.dto.UserRoleDTO;
import com.zjj.blog.entity.Role;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.RoleVO;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/17 20:52
 */
public interface RoleService extends IService<Role> {
    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<UserRoleDTO> listUserRoles();

    /**
     * 查询所有角色
     *
     * @param condition 条件
     * @return 角色列表
     */
    PageResult<RoleDTO> listRoles(ConditionVO condition);

    /**
     * 删除角色
     *
     * @param ids 角色id列表
     */
    void deleteRoles(List<Integer> ids);

    /**
     * 新增或修改角色
     *
     * @param roleVO 角色信息
     */
    void saveOrUpdateRole(RoleVO roleVO);
}
