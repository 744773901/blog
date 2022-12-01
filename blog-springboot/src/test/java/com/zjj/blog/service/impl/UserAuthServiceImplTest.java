package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjj.blog.dto.UserAreaDTO;
import com.zjj.blog.entity.UserAuth;
import com.zjj.blog.mapper.UserAuthMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAuthServiceImplTest {
    @Resource
    private UserAuthMapper userAuthMapper;

    @Test
    void listUserAreas() {
        List<UserAuth> list = userAuthMapper.selectList(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getIpSource));
        Map<String, Long> map = list.stream().map(item -> {
            if (StringUtils.isNotBlank(item.getIpSource())) {
                return item.getIpSource()
                        .replaceAll("省", "")
                        .replaceAll("市", "");
            }
            return "未知";
        }).collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        List<UserAreaDTO> userAreaDTOList = map.entrySet().stream()
                .map(item -> UserAreaDTO.builder()
                        .name(item.getKey())
                        .value(item.getValue())
                        .build())
                .collect(Collectors.toList());
        System.out.println(userAreaDTOList);
    }

    @Test
    public void test(){
        List<UserAuth> userAuths = userAuthMapper.selectList(null);
        userAuths.forEach(System.out::println);
    }
}