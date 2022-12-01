package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.dto.AdminTalkDTO;
import com.zjj.blog.dto.TalkDTO;
import com.zjj.blog.entity.Talk;
import com.zjj.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/19 20:10
 */
public interface TalkMapper extends BaseMapper<Talk> {

    /**
     * 查询后台说说
     *
     * @param current   当前页码
     * @param size      每页大小
     * @param condition 条件
     * @return 说说列表
     */
    List<AdminTalkDTO> selectAdminTalkList(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);

    /**
     * 通过说说id查询后台说说
     *
     * @param id 说说id
     * @return 说说信息
     */
    AdminTalkDTO selectAdminTalkById(@Param("id") Integer id);

    /**
     * 查询说说列表
     *
     * @param current 页码
     * @param size    大小
     * @return {@link List<TalkDTO>}
     */
    List<TalkDTO> listTalks(@Param("current") Long current, @Param("size") Long size);

    /**
     * 通过id查看说说
     *
     * @param talkId 说说id
     * @return {@link TalkDTO} 说说信息
     */
    TalkDTO getTalkById(@Param("talkId") Integer talkId);
}
