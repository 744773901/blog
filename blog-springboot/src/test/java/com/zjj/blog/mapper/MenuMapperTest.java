package com.zjj.blog.mapper;

import com.zjj.blog.dto.MenuDTO;
import com.zjj.blog.dto.UserMenuDTO;
import com.zjj.blog.entity.Menu;
import com.zjj.blog.service.MenuService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.vo.ConditionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.TRUE;

@SpringBootTest
class MenuMapperTest {

    @Resource
    private MenuMapper menuMapper;
    @Resource
    private MenuService menuService;

    @Test
    public void test1() {
//        listUserMenu().forEach(System.out::println);
        List<MenuDTO> list = menuService.listMenus(ConditionVO.builder().keyword(null).build());
        list.forEach(System.out::println);
    }

    @Test
    public void test2() {
        List<Menu> menuList = menuMapper.selectList(null);
        List<MenuDTO> menuDTOList = menuList.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), 0))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .map(menu -> {
                    MenuDTO menuDTO = BeanCopyUtil.copyObject(menu, MenuDTO.class);
                    List<Menu> children = menuList.stream()
                            .filter(child -> Objects.equals(child.getParentId(), menuDTO.getId()))
                            .sorted(Comparator.comparing(Menu::getOrderNum))
//                            .map(item -> BeanCopyUtil.copyObject(item, MenuDTO.class))
                            .collect(Collectors.toList());
                    List<MenuDTO> childList = BeanCopyUtil.copyList(children, MenuDTO.class);
                    menuDTO.setChildren(childList);
                    return menuDTO;
                })
                .collect(Collectors.toList());
        menuDTOList.forEach(System.out::println);
    }

    public List<UserMenuDTO> listUserMenu() {
        List<Menu> menuList = menuMapper.listMenuByUserInfoId(1);
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

    private List<Menu> getChildren2(List<Menu> menuList, Menu rootMenu) {
        return menuList.stream()
                .filter(menu -> Objects.equals(rootMenu.getId(), menu.getParentId()))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .map(menu -> Menu.builder().children(getChildren2(menuList, menu)).build())
                .collect(Collectors.toList());
    }
}