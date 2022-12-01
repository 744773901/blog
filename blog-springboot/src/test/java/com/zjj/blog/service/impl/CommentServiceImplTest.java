package com.zjj.blog.service.impl;

import com.zjj.blog.mapper.CommentMapper;
import com.zjj.blog.vo.AdminCommentVO;
import com.zjj.blog.vo.ConditionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
@SpringBootTest
class CommentServiceImplTest {
    @Resource
    private CommentMapper commentMapper;

    @Test
    void listAdminComment() {
        List<AdminCommentVO> list = commentMapper.listAdminComment(0L, 10L, ConditionVO.builder().type(1).keyword("管理").build());
        list.forEach(System.out::println);
    }
}