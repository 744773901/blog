package com.zjj.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/23 21:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "分页对象")
public class PageResult<T> {

    /**
     * 分页列表
     */
    @ApiModelProperty(name = "records", value = "分页列表", dataType = "List<T>")
    private List<T> records;

    /**
     * 总数
     */
    @ApiModelProperty(name = "count", value = "总数", dataType = "Integer")
    private Integer count;
}
