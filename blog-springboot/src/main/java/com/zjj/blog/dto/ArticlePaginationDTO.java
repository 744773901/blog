package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上下篇文章
 *
 * @author 知白守黑
 * @date 2022/8/29 15:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePaginationDTO {
    /**
     * 文章id
     */
    private Integer id;

    /**
     * 文章缩略图
     */
    private String articleCover;

    /**
     * 标题
     */
    private String articleTitle;
}
