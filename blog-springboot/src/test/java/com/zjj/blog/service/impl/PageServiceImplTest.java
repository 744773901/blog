package com.zjj.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.zjj.blog.constant.RedisConst;
import com.zjj.blog.mapper.PageMapper;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.vo.PageVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class PageServiceImplTest {
    @Resource
    private PageMapper pageMapper;
    @Resource
    private RedisService redisService;

    @Test
    void listPages() {
        List<PageVO> pageVOList = BeanCopyUtil.copyList(pageMapper.selectList(null), PageVO.class);
//        redisService.set(RedisConst.PAGE_COVER, pageVOList);
        Object o = redisService.get(RedisConst.PAGE_COVER);
        pageVOList = JSON.parseArray(o.toString(), PageVO.class);
        System.out.println(o);
        System.out.println(pageVOList);
    }
}