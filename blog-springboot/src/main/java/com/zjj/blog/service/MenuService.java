package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.LabelOptionDTO;
import com.zjj.blog.dto.MenuDTO;
import com.zjj.blog.dto.UserMenuDTO;
import com.zjj.blog.entity.Menu;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.MenuVO;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/25 13:38
 */
public interface MenuService extends IService<Menu> {

    /**
     * 查询用户菜单
     *
     * @return 菜单列表
     */
    List<UserMenuDTO> listUserMenu();

    /**
     * 查询后台菜单列表
     *
     * @param condition 条件
     * @return 菜单列表
     */
    List<MenuDTO> listMenus(ConditionVO condition);

    /**
     * 新增或修改菜单
     *
     * @param menuVO 菜单信息
     */
    void saveOrUpdateMenu(MenuVO menuVO);

    /**
     * 根据id删除菜单
     *
     * @param id 菜单id
     */
    void deleteMenu(Integer id);

    /**
     * 查看用户菜单
     *
     * @return 菜单列表
     */
    List<LabelOptionDTO> listMenuOptions();
}
