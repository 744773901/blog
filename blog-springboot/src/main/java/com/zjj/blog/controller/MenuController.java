package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.LabelOptionDTO;
import com.zjj.blog.dto.MenuDTO;
import com.zjj.blog.dto.UserMenuDTO;
import com.zjj.blog.service.MenuService;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.MenuVO;
import com.zjj.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/7/25 13:37
 */
@Api(tags = "菜单模块")
@RestController
public class MenuController {

    @Resource
    private MenuService menuService;

    /**
     * 查询登录用户菜单
     *
     * @return {@link UserMenuDTO} 用户菜单列表
     */
    @ApiOperation(value = "查询登录用户菜单")
    @GetMapping("/admin/user/menus")
    public Result<List<UserMenuDTO>> getUserMenus() {
        return Result.ok(menuService.listUserMenu());
    }

    /**
     * 查看角色菜单选项
     *
     * @return {@link Result<LabelOptionDTO>} 查看角色菜单选项
     */
    @ApiOperation(value = "查看角色菜单选项")
    @GetMapping("/admin/role/menus")
    public Result<List<LabelOptionDTO>> listMenuOptions() {
        return Result.ok(menuService.listMenuOptions());
    }

    /**
     * 查询后台菜单列表
     *
     * @param condition 条件
     * @return 菜单列表
     */
    @ApiOperation(value = "查询后台菜单列表")
    @GetMapping("/admin/menus")
    public Result<List<MenuDTO>> listMenus(ConditionVO condition) {
        return Result.ok(menuService.listMenus(condition));
    }

    /**
     * 新增或修改菜单
     *
     * @param menuVO 菜单信息
     * @return 菜单列表
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "新增或修改菜单")
    @PostMapping("/admin/menus")
    public Result<?> saveOrUpdateMenu(@Validated @RequestBody MenuVO menuVO) {
        menuService.saveOrUpdateMenu(menuVO);
        return Result.ok();
    }

    /**
     * 根据id删除菜单
     *
     * @param id 菜单id
     * @return {@link Result<>}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "根据id删除菜单")
    @DeleteMapping("/admin/menus/{id}")
    public Result<?> deleteMenu(@PathVariable("id") Integer id) {
        menuService.deleteMenu(id);
        return Result.ok();
    }
}
