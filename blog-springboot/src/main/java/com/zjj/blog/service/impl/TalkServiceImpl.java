package com.zjj.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.AdminTalkDTO;
import com.zjj.blog.dto.CommentCountDTO;
import com.zjj.blog.dto.TalkDTO;
import com.zjj.blog.entity.Talk;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.CommentMapper;
import com.zjj.blog.mapper.TalkMapper;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.service.TalkService;
import com.zjj.blog.utils.*;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.TalkVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.RedisConst.TALK_LIKE_COUNT;
import static com.zjj.blog.constant.RedisConst.USER_TALK_LIKE;
import static com.zjj.blog.enums.StatusEnum.PUBLIC;

/**
 * @author 知白守黑
 * @date 2022/8/19 20:24
 */
@Service
public class TalkServiceImpl extends ServiceImpl<TalkMapper, Talk> implements TalkService {
    @Resource
    private TalkMapper talkMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private RedisService redisService;

    @Override
    public List<String> listHomeTalks() {
        return talkMapper.selectList(new LambdaQueryWrapper<Talk>()
                        .eq(Talk::getStatus, PUBLIC.getStatus())
                        .orderByDesc(Talk::getIsTop)
                        .orderByDesc(Talk::getId)
                        .last("LIMIT 10"))
                .stream()
                .map(talk -> talk.getContent().length() > 200 ? HTMLUtil.deleteHMTLTag(talk.getContent().substring(0, 200)) : HTMLUtil.deleteHMTLTag(talk.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<TalkDTO> listTalks() {
        //查询说说总量
        int count = talkMapper.selectCount((new LambdaQueryWrapper<Talk>()
                .eq(Talk::getStatus, PUBLIC.getStatus()))).intValue();
        if (count == 0) {
            return new PageResult<>();
        }
        //分页查询说说
        List<TalkDTO> talkDTOList = talkMapper.listTalks(PageUtil.getLimitCurrent(), PageUtil.getSize());
        //查询说说评论量
        List<Integer> talkIdList = talkDTOList.stream()
                .map(TalkDTO::getId)
                .collect(Collectors.toList());
        Map<Integer, Integer> commentCountMap = commentMapper.listCommentCountByTopicIds(talkIdList)
                .stream()
                .collect(Collectors.toMap(CommentCountDTO::getId, CommentCountDTO::getCommentCount));
        //查询说说点赞量
        Map<String, Object> likeCountMap = redisService.hGetAll(TALK_LIKE_COUNT);
        talkDTOList.forEach(item -> {
            item.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
            item.setCommentCount(commentCountMap.get(item.getId()));
            //转换图片格式
            if (Objects.nonNull(item.getImages())) {
                item.setImgList(JSON.parseArray(item.getImages(), String.class));
            }
        });
        return new PageResult<>(talkDTOList, count);
    }

    @Override
    public void saveTalkLike(Integer talkId) {
        //判断是否点赞
        String talkLikeKey = USER_TALK_LIKE + UserUtil.getLoginUser().getUserInfoId();
        if (redisService.sIsMember(talkLikeKey, talkId)) {
            //点过赞则删除说说id
            redisService.sRemove(talkLikeKey, talkId);
            //说说点赞量-1
            redisService.hDecr(TALK_LIKE_COUNT, talkId.toString(), 1L);
        } else {
            //未点赞则增加说说id
            redisService.sAdd(talkLikeKey, talkId);
            //说说点赞量+1
            redisService.hIncr(TALK_LIKE_COUNT, talkId.toString(), 1L);
        }
    }

    @Override
    public TalkDTO getTalkById(Integer talkId) {
        //查询说说信息
        TalkDTO talkDTO = talkMapper.getTalkById(talkId);
        if (Objects.isNull(talkDTO)) {
            throw new BusinessException("说说不存在");
        }
        //查询说说点赞量
        talkDTO.setLikeCount((Integer) redisService.hGet(TALK_LIKE_COUNT, talkId.toString()));
        //转换图片格式
        if (Objects.nonNull(talkDTO.getImages())) {
            talkDTO.setImgList(JSON.parseArray(talkDTO.getImages(), String.class));
        }
        return talkDTO;
    }

    @Override
    public PageResult<AdminTalkDTO> listAdminTalk(ConditionVO condition) {
        int count = talkMapper.selectCount(new LambdaQueryWrapper<Talk>()
                        .eq(Objects.nonNull(condition.getStatus()), Talk::getStatus, condition.getStatus()))
                .intValue();
        if (count == 0) {
            return new PageResult<>();
        }
        List<AdminTalkDTO> adminTalkList = talkMapper.selectAdminTalkList(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        adminTalkList.forEach(item -> item.setImageList(JSON.parseArray(item.getImages(), String.class)));
        return new PageResult<>(adminTalkList, count);
    }

    @Override
    public void saveOrUpdateTalk(TalkVO talkVO) {
        Talk talk = BeanCopyUtil.copyObject(talkVO, Talk.class);
        talk.setUserId(UserUtil.getLoginUser().getUserInfoId());
        this.saveOrUpdate(talk);
    }

    @Override
    public AdminTalkDTO getAdminTalkById(Integer id) {
        AdminTalkDTO adminTalk = talkMapper.selectAdminTalkById(id);
        adminTalk.setImageList(JSON.parseArray(adminTalk.getImages(), String.class));
        return adminTalk;
    }
}
