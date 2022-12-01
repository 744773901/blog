package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.dto.UserInfoDTO;
import com.zjj.blog.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @author 知白守黑
 * @date 2022/7/16 16:33
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 查询用户信息
     *
     * @param userInfoId 用户信息id
     * @return 用户信息
     */
    UserInfoDTO getUserInfo(@Param("userInfoId") Integer userInfoId);
}
