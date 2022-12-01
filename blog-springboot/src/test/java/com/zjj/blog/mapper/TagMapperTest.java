package com.zjj.blog.mapper;

import com.zjj.blog.vo.ConditionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TagMapperTest {
    @Resource
    private TagMapper tagMapper;

    @Test
    void listAdminTag() {
        ConditionVO conditionVO = new ConditionVO();
        conditionVO.setKeyword("java");
        tagMapper.listAdminTag(0L, 10L, conditionVO).forEach(System.out::println);
    }
}