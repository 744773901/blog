package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 知白守黑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecallMessageDTO {

    /**
     * 消息id
     */
    private Integer id;

    /**
     * 是否是语音
     */
    private Boolean isVoice;
}
