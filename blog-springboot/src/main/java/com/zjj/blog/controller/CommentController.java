package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.CommentDTO;
import com.zjj.blog.dto.ReplyDTO;
import com.zjj.blog.service.CommentService;
import com.zjj.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/15 21:31
 */
@Api(tags = "评论模块")
@RestController
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 查询后台评论
     *
     * @param condition 条件
     * @return {@link Result}
     */
    @ApiOperation(value = "查询后台评论")
    @GetMapping("/admin/comments")
    public Result<PageResult<AdminCommentVO>> listAdminComment(ConditionVO condition) {
        return Result.ok(commentService.listAdminComment(condition));
    }

    /**
     * 审核评论
     *
     * @param review 审核信息
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "审核评论")
    @PutMapping("/admin/comments/review")
    public Result<?> updateCommentReview(@Validated @RequestBody ReviewVO review) {
        commentService.updateCommentReview(review);
        return Result.ok();
    }

    /**
     * 删除评论
     *
     * @param ids ID列表
     * @return {@link Result}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "删除评论")
    @DeleteMapping("/admin/comments")
    public Result<?> deleteComments(@RequestBody List<Integer> ids) {
        commentService.removeByIds(ids);
        return Result.ok();
    }

    /**
     * 查询评论
     *
     * @param commentVO 评论信息
     * @return {@link Result<CommentDTO>}
     */
    @ApiOperation(value = "查询评论")
    @GetMapping("/comments")
    public Result<PageResult<CommentDTO>> listComments(CommentVO commentVO) {
        return Result.ok(commentService.listComments(commentVO));
    }

    /**
     * 添加评论
     *
     * @param commentVO 评论信息
     * @return {@link Result<>}
     */
    @OperateLog(type = SAVE)
    @ApiOperation(value = "添加评论")
    @PostMapping("/comments")
    public Result<?> saveComment(@Validated @RequestBody CommentVO commentVO) {
        commentService.saveComment(commentVO);
        return Result.ok();
    }

    /**
     * 查询评论下的回复
     *
     * @param id 评论id
     * @return {@link Result<ReplyDTO>} 回复列表
     */
    @ApiOperation(value = "查询评论下的回复")
    @GetMapping("/comments/{id}/replies")
    public Result<List<ReplyDTO>> listRepliesById(@PathVariable("id") Integer id) {
        return Result.ok(commentService.listRepliesById(id));
    }

    /**
     * 评论点赞
     *
     * @param id 评论id
     * @return {@link Result<>}
     */
    @ApiOperation(value = "评论点赞")
    @PostMapping("/comments/{id}/like")
    public Result<?> saveCommentLike(@PathVariable("id") Integer id) {
        commentService.saveCommentLike(id);
        return Result.ok();
    }
}
