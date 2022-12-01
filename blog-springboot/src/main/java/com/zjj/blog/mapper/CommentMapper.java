package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.dto.CommentCountDTO;
import com.zjj.blog.dto.CommentDTO;
import com.zjj.blog.dto.ReplyCountDTO;
import com.zjj.blog.dto.ReplyDTO;
import com.zjj.blog.entity.Comment;
import com.zjj.blog.vo.AdminCommentVO;
import com.zjj.blog.vo.CommentVO;
import com.zjj.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/15 21:34
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询后台评论
     *
     * @param current   当前页
     * @param size      每页大小
     * @param condition 条件
     * @return 后台评论列表
     */
    List<AdminCommentVO> listAdminComment(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);

    /**
     * 查询后台评论数
     *
     * @param condition 条件
     * @return 后台评论数
     */
    Integer countAdminComment(@Param("condition") ConditionVO condition);

    /**
     * 查看评论
     *
     * @param current 当前页码
     * @param size    大小
     * @param comment 评论信息
     * @return 评论列表
     */
    List<CommentDTO> listComments(@Param("current") Long current, @Param("size") Long size, @Param("comment") CommentVO comment);

    /**
     * 根据评论id集合查询评论下的回复
     *
     * @param commentIdList 评论id集合
     * @return 回复集合
     */
    List<ReplyDTO> listReplies(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 根据评论id查询回复总量
     *
     * @param commentIdList 评论id集合
     * @return 回复数量
     */
    List<ReplyCountDTO> listReplyCountByCommentId(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 查看当条评论下的回复
     *
     * @param id      评论id
     * @param current 当前页码
     * @param size    大小
     * @return 回复集合
     */
    List<ReplyDTO> listRepliesById(@Param("current") Long current, @Param("size") Long size, @Param("id") Integer id);

    /**
     * 根据评论主题id获取评论量
     *
     * @param topicIdList 说说id列表
     * @return {@link List<CommentCountDTO>}说说评论量
     */
    List<CommentCountDTO> listCommentCountByTopicIds(List<Integer> topicIdList);
}
