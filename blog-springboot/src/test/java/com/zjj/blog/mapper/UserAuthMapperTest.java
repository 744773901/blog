package com.zjj.blog.mapper;

import com.zjj.blog.dto.AdminUserDTO;
import com.zjj.blog.vo.ConditionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class UserAuthMapperTest {
    @Resource
    private UserAuthMapper userAuthMapper;

    @Test
    void listAdminUser() {
        System.out.println(userAuthMapper.countUser(ConditionVO.builder().loginType(null).keyword("管理").build()));
        List<AdminUserDTO> listAdminUser = userAuthMapper.listAdminUser(0L, 10L, ConditionVO.builder().loginType(null).keyword(null).build());
        listAdminUser.forEach(System.out::println);
        listAdminUser.forEach(user -> {
            System.out.println(user.getNickname());
            user.getRoleList().forEach(System.out::println);
        });
    }
}