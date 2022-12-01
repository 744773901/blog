package com.zjj.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/15 22:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "审核")
public class ReviewVO {

    /**
     * 评论ID
     */
    @NotEmpty(message = "ID不能为空")
    @ApiModelProperty(value = "ids", name = "评论ID", required = true, dataType = "List<Integer>")
    private List<Integer> ids;

    /**
     * 审核状态
     */
    @NotNull(message = "审核状态不能为空")
    @ApiModelProperty(value = "isReview", name = "审核状态", required = true, dataType = "Integer")
    private Integer isReview;
}
