package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.LabelOptionDTO;
import com.zjj.blog.dto.MenuDTO;
import com.zjj.blog.dto.UserMenuDTO;
import com.zjj.blog.entity.Menu;
import com.zjj.blog.entity.RoleMenu;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.MenuMapper;
import com.zjj.blog.mapper.RoleMenuMapper;
import com.zjj.blog.service.MenuService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.UserUtil;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.MenuVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.FALSE;
import static com.zjj.blog.constant.CommonConst.TRUE;

/**
 * @author 知白守黑
 * @date 2022/7/25 13:38
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Resource
    private MenuMapper menuMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<UserMenuDTO> listUserMenu() {
        List<Menu> menuList = menuMapper.listMenuByUserInfoId(UserUtil.getLoginUser().getUserInfoId());
        return menuList.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), 0))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .map(menu -> {
                    UserMenuDTO userMenu = BeanCopyUtil.copyObject(menu, UserMenuDTO.class);
                    userMenu.setChildren(getChildren(menuList, menu));
                    userMenu.setHidden(menu.getIsHidden().equals(TRUE));
                    return userMenu;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取子菜单列表
     *
     * @param menuList 菜单列表
     * @param rootMenu 根菜单列表
     * @return 子菜单列表
     */
    private List<UserMenuDTO> getChildren(List<Menu> menuList, Menu rootMenu) {
        return menuList.stream()
                .filter(menu -> Objects.equals(rootMenu.getId(), menu.getParentId()))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .map(menu -> {
                    UserMenuDTO userMenu = BeanCopyUtil.copyObject(menu, UserMenuDTO.class);
                    userMenu.setChildren(getChildren(menuList, menu));
                    userMenu.setHidden(menu.getIsHidden().equals(TRUE));
                    return userMenu;
                })
                .collect(Collectors.toList());
    }

    private List<MenuDTO> getChildren2(List<Menu> menuList, Menu rootMenu) {
        return menuList.stream()
                .filter(menu -> Objects.equals(rootMenu.getId(), menu.getParentId()))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .map(menu -> {
                    MenuDTO menuDTO = BeanCopyUtil.copyObject(menu, MenuDTO.class);
                    menuDTO.setChildren(getChildren2(menuList, menu));
                    return menuDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LabelOptionDTO> listMenuOptions() {
        List<Menu> menuList = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .select(Menu::getId, Menu::getName, Menu::getParentId, Menu::getOrderNum));
        List<Menu> parentMenuList = menuList.stream()
                .filter(item -> Objects.equals(item.getParentId(), FALSE))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .collect(Collectors.toList());
        Map<Integer, List<Menu>> childrenListMap = menuList.stream()
                .filter(item -> !Objects.equals(item.getParentId(), FALSE))
                .collect(Collectors.groupingBy(Menu::getParentId));
        return parentMenuList.stream()
                .map(item -> {
                    List<LabelOptionDTO> list = new ArrayList<>();
                    List<Menu> children = childrenListMap.get(item.getId());
                    if (CollectionUtils.isNotEmpty(children)) {
                        list = children.stream()
                                .map(menu -> LabelOptionDTO.builder()
                                        .id(menu.getId())
                                        .label(menu.getName())
                                        .build())
                                .collect(Collectors.toList());
                    }
                    return LabelOptionDTO.builder()
                            .id(item.getId())
                            .label(item.getName())
                            .children(list)
                            .build();
                }).collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> listMenus(ConditionVO condition) {
        List<Menu> menuList = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .like(StringUtils.isNotBlank(condition.getKeyword()), Menu::getName, condition.getKeyword()));
        return menuList.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), 0))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .map(menu -> {
                    MenuDTO menuDTO = BeanCopyUtil.copyObject(menu, MenuDTO.class);
                    menuDTO.setChildren(getChildren2(menuList, menu));
                    return menuDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateMenu(MenuVO menuVO) {
        this.saveOrUpdate(BeanCopyUtil.copyObject(menuVO, Menu.class));
    }

    @Override
    public void deleteMenu(Integer id) {
        int count = roleMenuMapper.selectCount(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getMenuId, id)).intValue();
        if (count > 0) {
            throw new BusinessException("菜单有角色关联");
        }
        List<Integer> ids = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                        .select(Menu::getId)
                        .eq(Menu::getParentId, id))
                .stream()
                .map(Menu::getId)
                .collect(Collectors.toList());
        ids.add(id);
        menuMapper.deleteBatchIds(ids);
    }
}
