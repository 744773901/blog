package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 推荐文章
 *
 * @author 知白守黑
 * @date 2022/8/29 14:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRecommendDTO {
    /**
     * 文章id
     */
    private Integer id;

    /**
     * 文章缩略图
     */
    private String articleCover;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
