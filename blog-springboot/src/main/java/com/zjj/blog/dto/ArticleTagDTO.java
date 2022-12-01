package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章标签
 *
 * @author 知白守黑
 * @date 2022/8/10 20:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagDTO {

    /**
     * 标签ID
     */
    private Integer id;

    /**
     * 标签名称
     */
    private String tagName;
}
