package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问量
 * @author 知白守黑
 * @date 2022/11/19 22:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniqueViewDTO {

    /**
     * 日期
     */
    private String day;

    /**
     * 访问量
     */
    private Integer viewsCount;
}
