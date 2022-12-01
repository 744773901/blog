package com.zjj.blog.mapper;

import com.zjj.blog.dto.RoleDTO;
import com.zjj.blog.vo.ConditionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoleMapperTest {

    @Resource
    private RoleMapper roleMapper;

    @Test
    void listRolesByUserinfoId() {
        roleMapper.listRolesByUserinfoId(1).forEach(System.out::println);
    }

    @Test
    void listRoleResource() {
        roleMapper.listRoleResource().forEach(System.out::println);
    }

    @Test
    public void testListRoles(){
        List<RoleDTO> roleList = roleMapper.listRoles(0L, 10000L, ConditionVO.builder().keyword(null).build());
        roleList.forEach(System.out::println);
        for (RoleDTO roleDTO : roleList) {
            System.out.println(roleDTO.getMenuIdList());
            System.out.println(roleDTO.getResourceIdList());
        }
    }
}