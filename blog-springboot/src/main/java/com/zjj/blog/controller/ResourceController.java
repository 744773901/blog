package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.LabelOptionDTO;
import com.zjj.blog.dto.ResourceDTO;
import com.zjj.blog.service.ResourceService;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.ResourceVO;
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
 * @date 2022/8/25 20:03
 */
@Api(tags = "资源模块")
@RestController
public class ResourceController {
    @Resource
    private ResourceService resourceService;

    /**
     * 查看角色资源选项
     *
     * @return {@link Result<LabelOptionDTO>} 角色资源选项
     */
    @ApiOperation(value = "查看角色资源选项")
    @GetMapping("/admin/role/resources")
    public Result<List<LabelOptionDTO>> listResourceOption() {
        return Result.ok(resourceService.listResourceOption());
    }

    /**
     * 查询资源列表
     *
     * @return {@link Result} 资源列表
     */
    @ApiOperation(value = "查询资源列表")
    @GetMapping("/admin/resources")
    public Result<List<ResourceDTO>> listResources(ConditionVO condition) {
        return Result.ok(resourceService.listResources(condition));
    }

    /**
     * 根据id删除资源
     *
     * @param id 资源id
     * @return {@link Result<>}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "根据id删除资源")
    @DeleteMapping("/admin/resources/{id}")
    public Result<?> deleteResourceById(@PathVariable("id") Integer id) {
        resourceService.deleteResourceById(id);
        return Result.ok();
    }

    /**
     * 新增或修改资源
     *
     * @param resourceVO 资源信息
     * @return {@link Result<>}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "新增或修改资源")
    @PostMapping("/admin/resources")
    public Result<?> saveOrUpdateResource(@RequestBody @Validated ResourceVO resourceVO) {
        resourceService.saveOrUpdateResource(resourceVO);
        return Result.ok();
    }
}
