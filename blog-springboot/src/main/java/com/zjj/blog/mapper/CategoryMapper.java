package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.dto.CategoryDTO;
import com.zjj.blog.entity.Category;
import com.zjj.blog.vo.AdminCategoryVO;
import com.zjj.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/10 20:47
 */
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询后台分类列表
     *
     * @param current   页码
     * @param size      大小
     * @param condition 条件
     * @return {@link AdminCategoryVO}
     */
    List<AdminCategoryVO> listAdminCategory(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);

    /**
     * 查询分类和对应文章数量
     *
     * @return 分类列表
     */
    List<CategoryDTO> listCategories();
}
