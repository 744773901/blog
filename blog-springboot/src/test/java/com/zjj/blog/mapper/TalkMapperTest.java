package com.zjj.blog.mapper;

import com.alibaba.fastjson2.JSON;
import com.zjj.blog.dto.AdminTalkDTO;
import com.zjj.blog.service.TalkService;
import com.zjj.blog.vo.ConditionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class TalkMapperTest {
    @Resource
    private TalkMapper talkMapper;
    @Resource
    private TalkService talkService;

    @Test
    void selectAdminTalkList() {
        List<AdminTalkDTO> list = talkMapper.selectAdminTalkList(0L, 10L, ConditionVO.builder().status(null).build());
        list.forEach(item -> {
            item.setImageList(JSON.parseArray(item.getImages(), String.class));
        });
        for (AdminTalkDTO adminTalkDTO : list) {
            System.out.println(adminTalkDTO.getImageList());
        }
    }

    @Test
    void selectAdminTalkById() {
        AdminTalkDTO adminTalkDTO = talkMapper.selectAdminTalkById(51);
        String images = adminTalkDTO.getImages();
        System.out.println(images);
        List<String> list = JSON.parseArray(images, String.class);
        list.forEach(System.out::println);
    }
    @Test
    public void test1(){
        talkService.listHomeTalks().forEach(System.out::println);
    }
}