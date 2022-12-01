package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.AdminFriendLinkDTO;
import com.zjj.blog.dto.FriendLinkDTO;
import com.zjj.blog.entity.FriendLink;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.FriendLinkVO;
import com.zjj.blog.vo.PageResult;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/24 16:53
 */
public interface FriendLinkService extends IService<FriendLink> {
    /**
     * 查询后台友链列表
     *
     * @param condition 条件
     * @return 后台友链列表
     */
    PageResult<AdminFriendLinkDTO> listAdminFriendLink(ConditionVO condition);

    /**
     * 新增或修改友链
     *
     * @param friendLinkVO 友链信息
     */
    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);

    /**
     * 查询友链列表
     *
     * @return 友链列表
     */
    List<FriendLinkDTO> listFriendLinks();
}
