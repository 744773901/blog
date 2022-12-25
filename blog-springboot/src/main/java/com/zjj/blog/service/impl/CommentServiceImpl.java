package com.zjj.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.constant.CommonConst;
import com.zjj.blog.dto.CommentDTO;
import com.zjj.blog.dto.EmailDTO;
import com.zjj.blog.dto.ReplyCountDTO;
import com.zjj.blog.dto.ReplyDTO;
import com.zjj.blog.entity.Comment;
import com.zjj.blog.enums.CommentTypeEnum;
import com.zjj.blog.mapper.ArticleMapper;
import com.zjj.blog.mapper.CommentMapper;
import com.zjj.blog.mapper.TalkMapper;
import com.zjj.blog.mapper.UserInfoMapper;
import com.zjj.blog.service.BlogInfoService;
import com.zjj.blog.service.CommentService;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.utils.HTMLUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.utils.UserUtil;
import com.zjj.blog.vo.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.FALSE;
import static com.zjj.blog.constant.CommonConst.TRUE;
import static com.zjj.blog.constant.RabbitMQConst.EMAIL_EXCHANGE;
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
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private TalkMapper talkMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${website.url}")
    private String websiteUrl;

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
        //评论邮箱通知
        if (websiteConfig.getIsEmailNotice() == TRUE) {
            CompletableFuture.runAsync(() -> notice(comment));
        }
    }

    /**
     * 评论邮件通知
     *
     * @param comment 评论
     */
    public void notice(Comment comment) {
        Integer userId = CommonConst.BLOGGER_ID;
        Integer topicId = comment.getTopicId();
        CommentTypeEnum commentTypeEnum = Objects.requireNonNull(CommentTypeEnum.getCommentType(comment.getType()));
        if (Objects.nonNull(comment.getReplyUserId())) {
            userId = comment.getReplyUserId();
        } else {
            switch (commentTypeEnum) {
                case ARTICLE:
                    userId = articleMapper.selectById(topicId).getUserId();
                    break;
                case TALK:
                    userId = talkMapper.selectById(topicId).getUserId();
                    break;
                default:
                    break;
            }
        }
        EmailDTO emailDTO = new EmailDTO();
        String email;
        //根据评论是否审核处理邮件发送
        if (comment.getIsReview().equals(TRUE)) {
            email = userInfoMapper.selectById(userId).getEmail();
            if (StringUtils.isNotBlank(email)) {
                emailDTO.setEmail(email);
                emailDTO.setSubject("评论提醒");
                String url = websiteUrl + commentTypeEnum.getPath() + topicId;
                emailDTO.setContent("收到一条新评论, 请前往" + url + "\n查看");
            }
        } else {
            email = userInfoMapper.selectById(CommonConst.BLOGGER_ID).getEmail();
            emailDTO.setEmail(email);
            emailDTO.setSubject("评论审核提醒");
            emailDTO.setContent("收到一条新评论, 请前往博客后台管理审核");
        }
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
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
