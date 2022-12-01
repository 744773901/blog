package com.zjj.blog.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjj.blog.config.WebSocketConfig;
import com.zjj.blog.dto.ChatRecordDTO;
import com.zjj.blog.dto.RecallMessageDTO;
import com.zjj.blog.dto.WebsocketMessageDTO;
import com.zjj.blog.entity.ChatRecord;
import com.zjj.blog.enums.ChatTypeEnum;
import com.zjj.blog.enums.FilePathEnum;
import com.zjj.blog.mapper.ChatRecordMapper;
import com.zjj.blog.strategy.context.UploadStrategyContext;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.HTMLUtil;
import com.zjj.blog.utils.IPUtil;
import com.zjj.blog.vo.VoiceVO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author 知白守黑
 * @date 2022/10/5 19:11
 */
@Data
@Service
@ServerEndpoint(value = "/websocket", configurator = WebSocketConfig.class)
public class WebSocketServiceImpl {

    /**
     * 客户端用户session
     */
    private Session session;

    /**
     * 客户端用户session集合
     */
    private static CopyOnWriteArraySet<WebSocketServiceImpl> webSocketSet = new CopyOnWriteArraySet<>();

    private static ChatRecordMapper chatRecordMapper;
    private static UploadStrategyContext uploadStrategyContext;

    @Autowired
    public void setChatRecordMapper(ChatRecordMapper chatRecordMapper) {
        WebSocketServiceImpl.chatRecordMapper = chatRecordMapper;
    }

    @Autowired
    public void setUploadStrategyContext(UploadStrategyContext uploadStrategyContext) {
        WebSocketServiceImpl.uploadStrategyContext = uploadStrategyContext;
    }

    /**
     * 建立连接时调用
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        webSocketSet.add(this);
        updateOnlineCount();
        ChatRecordDTO chatRecordDTO = queryChatRecordHistory();
        WebsocketMessageDTO messageDTO = WebsocketMessageDTO.builder()
                .type(ChatTypeEnum.HISTORY_RECORD.getType())
                .data(chatRecordDTO)
                .build();
        synchronized (this.session) {
            this.session.getBasicRemote().sendText(JSON.toJSONString(messageDTO));
        }
    }

    /**
     * 关闭连接时调用
     */
    @OnClose
    public void onClose() throws IOException {
        // 更新在线用户
        webSocketSet.remove(this);
        updateOnlineCount();
    }

    /**
     * 发生异常时调用
     */
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    /**
     * 服务端接收消息时调用
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        WebsocketMessageDTO messageDTO = JSON.parseObject(message, WebsocketMessageDTO.class);
        switch (Objects.requireNonNull(ChatTypeEnum.getChatType(messageDTO.getType()))) {
            case SEND_MESSAGE:
                ChatRecord chatRecord = JSON.parseObject(JSON.toJSONString(messageDTO.getData()), ChatRecord.class);
                // 过滤内容
                chatRecord.setContent(HTMLUtil.filter(chatRecord.getContent()));
                // 保存聊天记录
                chatRecordMapper.insert(chatRecord);
                // 广播消息
                messageDTO.setData(chatRecord);
                broadcastMessage(messageDTO);
                break;
            case RECALL_MESSAGE:
                //获取撤回消息id
                RecallMessageDTO recallMessageDTO = JSON.parseObject(JSON.toJSONString(messageDTO.getData()), RecallMessageDTO.class);
                //删除消息
                chatRecordMapper.deleteById(recallMessageDTO.getId());
                // 广播消息
                broadcastMessage(messageDTO);
                break;
            case HEART_BEAT:
                // 心跳消息
                messageDTO.setData("pong");
                session.getBasicRemote().sendText(JSON.toJSONString(messageDTO));
                break;
            default:
                break;
        }
    }

    /**
     * 更新在线客户端数量
     */
    private void updateOnlineCount() throws IOException {
        WebsocketMessageDTO messageDTO = WebsocketMessageDTO.builder()
                .type(ChatTypeEnum.ONLINE_COUNT.getType())
                .data(webSocketSet.size())
                .build();
        broadcastMessage(messageDTO);
    }

    /**
     * 广播消息
     *
     * @param messageDTO {@link WebsocketMessageDTO}
     */
    private void broadcastMessage(WebsocketMessageDTO messageDTO) throws IOException {
        for (WebSocketServiceImpl webSocket : webSocketSet) {
            synchronized (webSocket.session) {
                webSocket.session.getBasicRemote().sendText(JSON.toJSONString(messageDTO));
            }
        }
    }

    /**
     * 查询历史聊天记录
     */
    private ChatRecordDTO queryChatRecordHistory() {
        // 查询12个小时前到此时的聊天记录
        List<ChatRecord> chatRecords = chatRecordMapper.selectList(new LambdaQueryWrapper<ChatRecord>()
                .ge(ChatRecord::getCreateTime, DateUtil.offsetHour(new Date(), -12)));

        // 获取客户端IP地址
        String ipAddress = this.session.getUserProperties().get(WebSocketConfig.HEADER_NAME).toString();
        return ChatRecordDTO.builder()
                .chatRecordList(chatRecords)
                .ipAddress(ipAddress)
                .ipSource(IPUtil.getIpSource(ipAddress))
                .build();
    }

    /**
     * 发送语音
     *
     * @param voiceVO 语音信息
     */
    public void sendVoice(VoiceVO voiceVO) {
        //上传语音
        String content = uploadStrategyContext.executeUploadStrategy(voiceVO.getFile(), FilePathEnum.VOICE.getPath());
        //保存语音聊天记录
        voiceVO.setContent(content);
        ChatRecord chatRecord = BeanCopyUtil.copyObject(voiceVO, ChatRecord.class);
        chatRecordMapper.insert(chatRecord);
        //发送语音消息
        WebsocketMessageDTO websocketMessageDTO = WebsocketMessageDTO.builder()
                .type(ChatTypeEnum.VOICE_MESSAGE.getType())
                .data(chatRecord)
                .build();
        try {
            broadcastMessage(websocketMessageDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
