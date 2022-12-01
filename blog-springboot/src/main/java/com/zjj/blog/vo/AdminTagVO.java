package com.zjj.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 知白守黑
 * @date 2022/8/14 21:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTagVO {

    /**
     * 标签id
     */
    private Integer id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
