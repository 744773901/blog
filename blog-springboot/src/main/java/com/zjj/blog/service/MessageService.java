package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.MessageDTO;
import com.zjj.blog.vo.*;
import com.zjj.blog.entity.Message;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/16 16:19
 */
public interface MessageService extends IService<Message> {

    /**
     * 查询后台留言
     *
     * @param condition 条件
     * @return 留言列表
     */
    PageResult<AdminMessageVO> listAdminMessage(ConditionVO condition);

    /**
     * 审核留言
     *
     * @param reviewVO 审核信息
     */
    void updateMessageReview(ReviewVO reviewVO);

    /**
     * 查询留言
     *
     * @return 留言列表
     */
    List<MessageDTO> listMessages();

    /**
     * 添加留言
     *
     * @param messageVO 留言信息
     */
    void saveMessage(MessageVO messageVO);
}
