package com.zjj.blog.mapper;

import com.zjj.blog.entity.Category;
import com.zjj.blog.service.CategoryService;
import com.zjj.blog.vo.ConditionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
class CategoryMapperTest {


    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private CategoryService categoryService;

    @Test
    void listAdminCategory() {
        ConditionVO conditionVO = new ConditionVO();
        conditionVO.setKeyword("分类");
        categoryMapper.listAdminCategory(0L, 10L, conditionVO).forEach(System.out::println);
    }

    @Test
    public void testSaveUpdateCategory() {
//        System.out.println(categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
//                .select(Category::getId, Category::getCategoryName)
//                .eq(Category::getCategoryName, "Java")));
        categoryService.saveOrUpdate(Category.builder()
                .id(191)
                .categoryName("测试分类22")
                .build());
    }
}