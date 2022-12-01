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
 * @date 2022/8/12 19:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "逻辑删除")
public class DeleteVO {

    /**
     * id列表
     */
    @NotEmpty(message = "id不能为空")
    @ApiModelProperty(name = "ids", value = "id列表", required = true, dataType = "List<Integer>")
    private List<Integer> ids;

    /**
     * 逻辑删除标记
     */
    @NotNull(message = "删除状态不能为空")
    @ApiModelProperty(name = "isDelete", value = "逻辑删除标记", required = true, dataType = "Integer")
    private Integer isDelete;
}
