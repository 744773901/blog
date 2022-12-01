package com.zjj.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 知白守黑
 * @date 2022/8/16 15:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMessageVO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 留言用户昵称
     */
    private String nickname;

    /**
     * 留言用户头像
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String messageContent;

    /**
     * 用户IP地址
     */
    private String ipAddress;

    /**
     * 用户IP来源
     */
    private String ipSource;

    /**
     * 审核状态
     */
    private Integer isReview;

    /**
     * 留言时间
     */
    private LocalDateTime createTime;
}
