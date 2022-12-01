package com.zjj.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 知白守黑
 * @date 2022/10/5 20:18
 */
@Getter
@AllArgsConstructor
public enum ChatTypeEnum {
    /**
     * 在线人数
     */
    ONLINE_COUNT(1, "在线人数"),
    /**
     * 历史记录
     */
    HISTORY_RECORD(2, "历史记录"),
    /**
     * 发送消息
     */
    SEND_MESSAGE(3, "发送消息"),
    /**
     * 撤回消息
     */
    RECALL_MESSAGE(4, "撤回消息"),
    /**
     * 语音消息
     */
    VOICE_MESSAGE(5, "语音消息"),
    /**
     * 心跳消息
     */
    HEART_BEAT(6, "心跳消息");

    /**
     * 类型
     */
    private Integer type;

    /**
     * 描述
     */
    private String desc;

    public static ChatTypeEnum getChatType(Integer type) {
        for (ChatTypeEnum value : ChatTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
