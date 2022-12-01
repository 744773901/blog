package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 知白守黑
 * @date 2022/8/27 13:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAreaDTO {
    /**
     * 地区名
     */
    private String name;

    /**
     * 数量
     */
    private Long value;
}
