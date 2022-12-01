package com.zjj.blog.dto;

import com.zjj.blog.entity.ChatRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/10/6 17:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecordDTO {
    /**
     * 聊天记录
     */
    private List<ChatRecord> chatRecordList;
    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * IP来源
     */
    private String ipSource;
}
