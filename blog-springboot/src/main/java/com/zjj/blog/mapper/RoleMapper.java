package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.dto.RoleDTO;
import com.zjj.blog.dto.RoleResourceDTO;
import com.zjj.blog.entity.Role;
import com.zjj.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/16 16:34
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 通过用户信息id获取角色
     *
     * @param userInfoId 用户信息id
     * @return 角色集合
     */
    List<String> listRolesByUserinfoId(Integer userInfoId);

    /**
     * 查询角色资源权限集合
     *
     * @return {@link RoleResourceDTO}
     */
    List<RoleResourceDTO> listRoleResource();

    /**
     * 查询所有角色
     *
     * @param current   页码
     * @param size      大小
     * @param condition 条件
     * @return 角色列表
     */
    List<RoleDTO> listRoles(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);
}
