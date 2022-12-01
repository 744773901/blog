package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/25 13:40
 */

public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 通过用户信息id查询用户菜单
     *
     * @param userInfoId 用户信息id
     * @return 菜单列表
     */
    List<Menu> listMenuByUserInfoId(@Param("userInfoId") Integer userInfoId);
}
