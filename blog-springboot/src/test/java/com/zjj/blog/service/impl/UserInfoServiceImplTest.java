package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjj.blog.constant.RedisConst;
import com.zjj.blog.dto.UserOnlineDTO;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.service.UserInfoService;
import com.zjj.blog.utils.BeanCopyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class UserInfoServiceImplTest {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisService redisService;

    @Test
    void getUserInfo() {
        String keyword = "理管";
        List<UserOnlineDTO> userOnlineList = new ArrayList<>();
        redisService.keys(RedisConst.LOGIN_USER_KEY + "*").forEach(key -> {
//            Object o = redisService.get(key);
            UserOnlineDTO userOnlineDTO = BeanCopyUtil.copyObject(redisService.get(key), UserOnlineDTO.class);
            userOnlineList.add(userOnlineDTO);
        });
        List<UserOnlineDTO> userList = userOnlineList.stream()
                .filter(item -> StringUtils.isBlank(keyword) || item.getNickname().contains(keyword))
                .sorted(Comparator.comparing(UserOnlineDTO::getLastLoginTime).reversed())
                .collect(Collectors.toList());
        userList.forEach(System.out::println);
    }

    @Test
    public void testOffline(){
        System.out.println(redisService.del(RedisConst.LOGIN_USER_KEY + 2));
    }

    @Test
    public void testPage() {
//        List<Integer> list = Arrays.asList(
//                1, 2, 3,
//                4, 5, 6,
//                7, 8, 9,
//                10, 11, 12);
        List<String> list = new ArrayList<>();
        list.add("刘一");
        list.add("陈二");
        list.add("张三");
        list.add("李四");
        list.add("王五");
        list.add("赵六");
        list.add("孙七");
        list.add("周八");
        list.add("吴九");
        list.add("郑十");
        list.add("不知道了。。。");
        int size = list.size();
        int pageSize = 3;
        int current = 2;
        List<String> records = list.stream()
                .skip((current - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
        System.out.println(records);
        records.forEach(System.out::println);
    }
}