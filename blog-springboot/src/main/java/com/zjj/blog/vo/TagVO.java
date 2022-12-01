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
 * @date 2022/8/14 22:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "标签")
public class TagVO {

    /**
     * 标签ID
     */
    @ApiModelProperty(value = "id", name = "标签ID", dataType = "Integer")
    private Integer id;

    /**
     * 标签名称
     */
    @NotBlank(message = "分类名称不能为空")
    @ApiModelProperty(value = "tagName", name = "标签名称", required = true, dataType = "String")
    private String tagName;
}
