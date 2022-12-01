package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.CommentDTO;
import com.zjj.blog.dto.ReplyCountDTO;
import com.zjj.blog.dto.ReplyDTO;
import com.zjj.blog.entity.Comment;
import com.zjj.blog.mapper.CommentMapper;
import com.zjj.blog.service.BlogInfoService;
import com.zjj.blog.service.CommentService;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.utils.HTMLUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.utils.UserUtil;
import com.zjj.blog.vo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.FALSE;
import static com.zjj.blog.constant.CommonConst.TRUE;
import static com.zjj.blog.constant.RedisConst.COMMENT_LIKE_COUNT;
import static com.zjj.blog.constant.RedisConst.USER_COMMENT_LIKE;

/**
 * @author 知白守黑
 * @date 2022/8/15 21:35
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private BlogInfoService blogInfoService;

    @Override
    public PageResult<AdminCommentVO> listAdminComment(ConditionVO condition) {
        Integer count = commentMapper.countAdminComment(condition);
        if (count == 0) {
            return new PageResult<>();
        }
        List<AdminCommentVO> adminCommentList = commentMapper.listAdminComment(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        return new PageResult<>(adminCommentList, count);
    }

    @Override
    public void updateCommentReview(ReviewVO review) {
        List<Comment> commentList = review.getIds().stream().map(id -> Comment.builder()
                        .id(id)
                        .isReview(review.getIsReview())
                        .build())
                .collect(Collectors.toList());
        this.updateBatchById(commentList);
    }

    @Override
    public PageResult<CommentDTO> listComments(CommentVO commentVO) {
        // 查询评论量
        int commentCount = commentMapper.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(Objects.nonNull(commentVO.getTopicId()), Comment::getTopicId, commentVO.getTopicId())
                .eq(Comment::getType, commentVO.getType())
                .isNull(Comment::getParentId)
                .eq(Comment::getIsReview, TRUE)).intValue();
        if (commentCount == 0) {
            return new PageResult<>();
        }
        //分页查询评论数据
        List<CommentDTO> commentDTOList = commentMapper.listComments(PageUtil.getLimitCurrent(), PageUtil.getSize(), commentVO);
        if (CollectionUtils.isEmpty(commentDTOList)) {
            return new PageResult<>();
        }
        //查询redis的评论点赞数据
        Map<String, Object> likeCountMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        //拿到评论id集合
        List<Integer> commentIdList = commentDTOList.stream()
                .map(CommentDTO::getId)
                .collect(Collectors.toList());
        //根据评论id集合查询评论回复数据
        List<ReplyDTO> replyDTOList = commentMapper.listReplies(commentIdList);
        //封装回复点赞量
        replyDTOList.forEach(item -> item.setLikeCount((Integer) likeCountMap.get(item.getId().toString())));
        //根据评论id分组回复数据
        Map<Integer, List<ReplyDTO>> replyMap = replyDTOList.stream()
                .collect(Collectors.groupingBy(ReplyDTO::getParentId));
        //根据评论id查询回复量
        Map<Integer, Integer> replyCountMap = commentMapper.listReplyCountByCommentId(commentIdList)
                .stream().collect(Collectors.toMap(ReplyCountDTO::getCommentId, ReplyCountDTO::getReplyCount));
        //封装评论数据
        commentDTOList.forEach(item -> {
            item.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
            item.setReplyDTOList(replyMap.get(item.getId()));
            item.setReplyCount(replyCountMap.get(item.getId()));
        });
        return new PageResult<>(commentDTOList, commentCount);
    }

    @Override
    public void saveComment(CommentVO commentVO) {
        //查询是否开启评论审核
        WebsiteConfigVO websiteConfig = blogInfoService.getWebsiteConfig();
        Integer isCommentReview = websiteConfig.getIsCommentReview();
        //过滤标签
        commentVO.setCommentContent(HTMLUtil.filter(commentVO.getCommentContent()));
        Comment comment = Comment.builder()
                .userId(UserUtil.getLoginUser().getUserInfoId())
                .replyUserId(commentVO.getReplyUserId())
                .topicId(commentVO.getTopicId())
                .commentContent(commentVO.getCommentContent())
                .parentId(commentVO.getParentId())
                .type(commentVO.getType())
                .isReview(isCommentReview == TRUE ? FALSE : TRUE)
                .build();
        commentMapper.insert(comment);
        //TODO 评论邮箱通知
    }

    @Override
    public List<ReplyDTO> listRepliesById(Integer id) {
        //转换页码查询评论下的回复
        List<ReplyDTO> replyDTOList = commentMapper.listRepliesById(PageUtil.getLimitCurrent(), PageUtil.getSize(), id);
        //查询redis的评论点赞数据
        Map<String, Object> likeCountMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        //封装点赞数据
        replyDTOList.forEach(item -> item.setLikeCount((Integer) likeCountMap.get(item.getId().toString())));
        return replyDTOList;
    }

    @Override
    public void saveCommentLike(Integer id) {
        //判断是否点过赞
        String commentLikeKey = USER_COMMENT_LIKE + UserUtil.getLoginUser().getUserInfoId();
        if (redisService.sIsMember(commentLikeKey, id)) {
            //点过赞则删除评论id
            redisService.sRemove(commentLikeKey, id);
            //评论点赞量-1
            redisService.hDecr(COMMENT_LIKE_COUNT, id.toString(), 1L);
        } else {
            //未点赞则增加评论id
            redisService.sAdd(commentLikeKey, id);
            //评论点赞量+1
            redisService.hIncr(COMMENT_LIKE_COUNT, id.toString(), 1L);
        }
    }
}
