package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.entity.UserAuth;
import com.zjj.blog.dto.AdminUserDTO;
import com.zjj.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/15 21:40
 */
public interface UserAuthMapper extends BaseMapper<UserAuth> {

    /**
     * 查询后台用户
     *
     * @param current   当前页码
     * @param size      每页大小
     * @param condition 条件
     * @return 后台用户列表
     */
    List<AdminUserDTO> listAdminUser(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);

    /**
     * 查询后台用户数量
     *
     * @param condition 条件
     * @return 后台用户数量
     */
    Integer countUser(@Param("condition") ConditionVO condition);
}
