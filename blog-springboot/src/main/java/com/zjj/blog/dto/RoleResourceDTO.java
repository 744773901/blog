package com.zjj.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色资源
 *
 * @author 知白守黑
 * @date 2022/7/22 19:35
 */
@Data
public class RoleResourceDTO {

    /**
     * 资源id
     */
    private Integer id;
    /**
     * 资源路径
     */
    private String url;
    /**
     * 请求方式
     */
    private String requestMethod;
    /**
     * 角色
     */
    private List<String> roleList;
}
