package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.AdminTalkDTO;
import com.zjj.blog.dto.TalkDTO;
import com.zjj.blog.entity.Talk;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.TalkVO;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/19 20:24
 */
public interface TalkService extends IService<Talk> {
    /**
     * 查询后台说说
     *
     * @param condition 条件
     * @return 说说列表
     */
    PageResult<AdminTalkDTO> listAdminTalk(ConditionVO condition);

    /**
     * 发布或修改说说
     *
     * @param talkVO 说说信息
     */
    void saveOrUpdateTalk(TalkVO talkVO);

    /**
     * 根据id查询后台说说
     *
     * @param id 说说id
     * @return 说说
     */
    AdminTalkDTO getAdminTalkById(Integer id);

    /**
     * 查询首页说说
     *
     * @return 首页说说列表
     */
    List<String> listHomeTalks();

    /**
     * 查询说说列表
     *
     * @return 说说列表
     */
    PageResult<TalkDTO> listTalks();

    /**
     * 点赞说说
     *
     * @param talkId 说说id
     */
    void saveTalkLike(Integer talkId);

    /**
     * 通过id查询说说
     *
     * @param talkId 说说id
     * @return 说说信息
     */
    TalkDTO getTalkById(Integer talkId);
}
