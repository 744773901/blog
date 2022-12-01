package com.zjj.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author 知白守黑
 * @date 2022/11/13 22:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户注册信息")
public class UserVO {

    /**
     * 用户名
     */
    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    @ApiModelProperty(value = "username", name = "用户名", required = true, dataType = "String")
    private String username;

    /**
     * 密码
     */
    @Size(min = 6, message = "密码不能少于六位")
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "password", name = "密码", required = true, dataType = "String")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "code", name = "验证码", required = true, dataType = "String")
    private String code;
}
