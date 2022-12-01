package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.CommentDTO;
import com.zjj.blog.dto.ReplyDTO;
import com.zjj.blog.entity.Comment;
import com.zjj.blog.vo.*;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/15 21:35
 */
public interface CommentService extends IService<Comment> {

    /**
     * 查询后台评论
     *
     * @param condition 条件
     * @return 评论列表
     */
    PageResult<AdminCommentVO> listAdminComment(ConditionVO condition);

    /**
     * 审核评论
     *
     * @param review 审核信息
     */
    void updateCommentReview(ReviewVO review);

    /**
     * 查询评论
     *
     * @param commentVO 评论信息
     * @return 评论
     */
    PageResult<CommentDTO> listComments(CommentVO commentVO);

    /**
     * 发表评论
     *
     * @param commentVO 评论信息
     */
    void saveComment(CommentVO commentVO);

    /**
     * 通过评论id查看评论下的回复
     *
     * @param id 评论id
     * @return 回复列表
     */
    List<ReplyDTO> listRepliesById(Integer id);

    /**
     * 点赞评论
     *
     * @param id 评论id
     */
    void saveCommentLike(Integer id);
}
