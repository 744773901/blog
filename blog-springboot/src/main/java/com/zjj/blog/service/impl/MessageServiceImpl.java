package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.MessageDTO;
import com.zjj.blog.entity.Message;
import com.zjj.blog.mapper.MessageMapper;
import com.zjj.blog.service.BlogInfoService;
import com.zjj.blog.service.MessageService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.HTMLUtil;
import com.zjj.blog.utils.IPUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.vo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.zjj.blog.constant.CommonConst.FALSE;
import static com.zjj.blog.constant.CommonConst.TRUE;

/**
 * @author 知白守黑
 * @date 2022/8/16 16:20
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private BlogInfoService blogInfoService;
    @Resource
    private HttpServletRequest request;

    @Override
    public List<MessageDTO> listMessages() {
        List<Message> messageList = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .select(Message::getId, Message::getNickname, Message::getAvatar, Message::getMessageContent, Message::getTime)
                .eq(Message::getIsReview, TRUE));
        return BeanCopyUtil.copyList(messageList, MessageDTO.class);
    }

    @Override
    public void saveMessage(MessageVO messageVO) {
        //判断是否需要审核
        Integer isReview = blogInfoService.getWebsiteConfig().getIsMessageReview();
        //获取用户ip
        String ipAddress = IPUtil.getIpAddress(request);
        String ipSource = IPUtil.getIpSource(ipAddress);
        Message message = BeanCopyUtil.copyObject(messageVO, Message.class);
        message.setMessageContent(HTMLUtil.filter(message.getMessageContent()));
        message.setIpAddress(ipAddress);
        message.setIsReview(isReview == TRUE ? FALSE : TRUE);
        message.setIpSource(ipSource);
        messageMapper.insert(message);
    }

    @Override
    public PageResult<AdminMessageVO> listAdminMessage(ConditionVO condition) {
        Page<Message> page = new Page<>(PageUtil.getCurrent(), PageUtil.getSize());
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .like(StringUtils.isNotBlank(condition.getKeyword()), Message::getNickname, condition.getKeyword())
                .eq(Objects.nonNull(condition.getIsReview()), Message::getIsReview, condition.getIsReview())
                .orderByDesc(Message::getId);
        Page<Message> messagePage = messageMapper.selectPage(page, wrapper);
        List<AdminMessageVO> adminMessageList = BeanCopyUtil.copyList(messagePage.getRecords(), AdminMessageVO.class);
        int count = (int) messagePage.getTotal();
        return new PageResult<>(adminMessageList, count);
    }

    @Override
    public void updateMessageReview(ReviewVO reviewVO) {
        List<Message> messageList = reviewVO.getIds().stream().map(id -> Message.builder()
                        .id(id)
                        .isReview(reviewVO.getIsReview())
                        .build())
                .collect(Collectors.toList());
        this.updateBatchById(messageList);
    }
}
