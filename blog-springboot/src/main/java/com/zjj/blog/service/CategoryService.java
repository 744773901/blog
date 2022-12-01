package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.CategoryDTO;
import com.zjj.blog.dto.CategoryOptionDTO;
import com.zjj.blog.entity.Category;
import com.zjj.blog.vo.AdminCategoryVO;
import com.zjj.blog.vo.CategoryVO;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/10 20:46
 */
public interface CategoryService extends IService<Category> {

    /**
     * 条件查询文章分类
     *
     * @param condition 条件
     * @return 文章分类列表
     */
    List<CategoryOptionDTO> listCategoryByOption(ConditionVO condition);

    /**
     * 查询后台分类
     *
     * @param condition 条件
     * @return {@link PageResult}
     */
    PageResult<AdminCategoryVO> listAdminCategory(ConditionVO condition);

    /**
     * 新增或修改分类
     *
     * @param categoryVO 分类
     */
    void saveOrUpdateCategory(CategoryVO categoryVO);

    /**
     * 删除分类
     *
     * @param ids id
     */
    void deleteCategory(List<Integer> ids);

    /**
     * 查询分类列表
     *
     * @return 分类列表
     */
    PageResult<CategoryDTO> listCategories();
}
