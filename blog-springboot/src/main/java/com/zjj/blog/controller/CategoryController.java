package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.CategoryDTO;
import com.zjj.blog.dto.CategoryOptionDTO;
import com.zjj.blog.service.CategoryService;
import com.zjj.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/10 20:44
 */
@Api(tags = "分类模块")
@RestController
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 查询分类列表
     *
     * @return {@link Result<CategoryDTO>} 分类列表
     */
    @ApiOperation(value = "查询分类列表")
    @GetMapping("/categories")
    public Result<PageResult<CategoryDTO>> listCategories() {
        return Result.ok(categoryService.listCategories());
    }

    /**
     * 条件查询文章分类
     *
     * @return {@link Result<>} 分类列表
     */
    @ApiOperation(value = "条件查询文章分类")
    @GetMapping("/admin/category/option")
    public Result<List<CategoryOptionDTO>> listCategoryByOption(ConditionVO condition) {
        return Result.ok(categoryService.listCategoryByOption(condition));
    }

    /**
     * 查询后台分类
     *
     * @param condition 条件
     * @return {@link Result}
     */
    @ApiOperation(value = "查询后台分类")
    @GetMapping("/admin/categories")
    public Result<PageResult<AdminCategoryVO>> listAdminCategory(ConditionVO condition) {
        return Result.ok(categoryService.listAdminCategory(condition));
    }

    /**
     * 新增或修改分类
     *
     * @param categoryVO 分类
     * @return {@link Result}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "新增或修改分类")
    @PostMapping("/admin/category")
    public Result<?> saveOrUpdateCategory(@Validated @RequestBody CategoryVO categoryVO) {
        categoryService.saveOrUpdateCategory(categoryVO);
        return Result.ok();
    }

    /**
     * 删除分类
     *
     * @param ids 分类ID
     * @return {@link Result}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "删除分类")
    @DeleteMapping("/admin/category")
    public Result<?> deleteCategory(@RequestBody List<Integer> ids) {
        categoryService.deleteCategory(ids);
        return Result.ok();
    }
}
