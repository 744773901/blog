package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章统计
 *
 * @author 知白守黑
 * @date 2022/11/19 22:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleStatisticsDTO {

    /**
     * 日期
     */
    private String date;

    /**
     * 数量
     */
    private Integer count;
}
