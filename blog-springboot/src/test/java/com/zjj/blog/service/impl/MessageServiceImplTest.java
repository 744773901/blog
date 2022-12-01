package com.zjj.blog.service.impl;

import com.zjj.blog.service.MessageService;
import com.zjj.blog.vo.AdminMessageVO;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MessageServiceImplTest {

    @Resource
    private MessageService messageService;

    @Test
    void listAdminMessage() {
        PageResult<AdminMessageVO> pageResult = messageService.listAdminMessage(ConditionVO.builder().keyword("测试").isReview(1).build());
        pageResult.getRecords().forEach(System.out::println);
        System.out.println(pageResult.getCount());
    }
}