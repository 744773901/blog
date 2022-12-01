package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.RoleDTO;
import com.zjj.blog.dto.UserRoleDTO;
import com.zjj.blog.service.RoleService;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.Result;
import com.zjj.blog.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/17 20:55
 */
@Api(tags = "角色模块")
@RestController
public class RoleController {
    @Resource
    private RoleService roleService;

    /**
     * 查询所有角色
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "查询所有角色")
    @GetMapping("/admin/user/roles")
    public Result<List<UserRoleDTO>> listUserRoles() {
        return Result.ok(roleService.listUserRoles());
    }

    /**
     * 查询所有角色
     *
     * @param condition 条件
     * @return {@link Result<RoleDTO>} 角色列表
     */
    @ApiOperation(value = "查询所有角色")
    @GetMapping("/admin/roles")
    public Result<PageResult<RoleDTO>> listRoles(ConditionVO condition) {
        return Result.ok(roleService.listRoles(condition));
    }

    /**
     * 删除角色
     *
     * @param ids 角色id列表
     * @return {@link Result<>}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "删除角色")
    @DeleteMapping("/admin/roles")
    public Result<?> deleteRoles(@RequestBody List<Integer> ids) {
        roleService.deleteRoles(ids);
        return Result.ok();
    }

    /**
     * 新增或修改角色
     *
     * @param roleVO 角色信息
     * @return {@link Result<>}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "新增或修改角色")
    @PostMapping("/admin/role")
    public Result<?> saveOrUpdateRole(@RequestBody @Validated RoleVO roleVO) {
        roleService.saveOrUpdateRole(roleVO);
        return Result.ok();
    }
}
