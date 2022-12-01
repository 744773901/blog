package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.CategoryDTO;
import com.zjj.blog.dto.CategoryOptionDTO;
import com.zjj.blog.entity.Article;
import com.zjj.blog.entity.Category;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.ArticleMapper;
import com.zjj.blog.mapper.CategoryMapper;
import com.zjj.blog.service.CategoryService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.vo.AdminCategoryVO;
import com.zjj.blog.vo.CategoryVO;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author 知白守黑
 * @date 2022/8/10 20:47
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public PageResult<CategoryDTO> listCategories() {
        return new PageResult<>(categoryMapper.listCategories(), categoryMapper.selectCount(null).intValue());
    }

    public List<CategoryOptionDTO> listCategoryByOption(ConditionVO condition) {
        //根据关键字条件查询文章分类 条件为空则按ID降序查询所有
        List<Category> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .like(StringUtils.isNotBlank(condition.getKeyword()), Category::getCategoryName, condition.getKeyword())
                .orderByDesc(Category::getId)
        );
        return BeanCopyUtil.copyList(categoryList, CategoryOptionDTO.class);
    }

    @Override
    public PageResult<AdminCategoryVO> listAdminCategory(ConditionVO condition) {
        int count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                        .like(StringUtils.isNotBlank(condition.getKeyword()), Category::getCategoryName, condition.getKeyword()))
                .intValue();
        if (count == 0) {
            return new PageResult<>();
        }
        List<AdminCategoryVO> categoryList = categoryMapper.listAdminCategory(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        return new PageResult<>(categoryList, count);
    }

    @Override
    public void saveOrUpdateCategory(CategoryVO categoryVO) {
        Category existCategory = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .select(Category::getId)
                .eq(Category::getCategoryName, categoryVO.getCategoryName()));
        if (Objects.nonNull(existCategory) && !existCategory.getId().equals(categoryVO.getId())) {
            throw new BusinessException("分类已存在");
        }
        Category category = Category.builder()
                .id(categoryVO.getId())
                .categoryName(categoryVO.getCategoryName())
                .build();
        this.saveOrUpdate(category);
    }

    @Override
    public void deleteCategory(List<Integer> ids) {
        Long count = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                .in(Article::getCategoryId, ids));
        if (count > 0) {
            throw new BusinessException("删除失败, 该分类下有文章");
        }
        categoryMapper.deleteBatchIds(ids);
    }
}
