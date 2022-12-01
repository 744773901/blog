package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author 知白守黑
 * @date 2022/8/10 23:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTopDTO {

    /**
     * 文章ID
     */
    @NotNull(message = "id不能为空")
    private Integer id;

    /**
     * 是否置顶
     */
    @NotNull(message = "置顶状态不能为空")
    private Integer isTop;
}
