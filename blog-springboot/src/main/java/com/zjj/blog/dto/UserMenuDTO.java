package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/7/25 13:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMenuDTO {

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 组件
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否隐藏
     */
    private Boolean Hidden;

    /**
     * 子菜单
     */
    private List<UserMenuDTO> children;
}
