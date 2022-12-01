package com.zjj.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author 知白守黑
 * @date 2022/8/14 18:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "分类")
public class CategoryVO {

    /**
     * 分类ID
     */
    @ApiModelProperty(value = "id", name = "分类ID", dataType = "Integer")
    private Integer id;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @ApiModelProperty(value = "categoryName", name = "分类名称", required = true, dataType = "String")
    private String categoryName;
}
