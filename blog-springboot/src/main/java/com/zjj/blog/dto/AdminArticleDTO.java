package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台文章
 *
 * @author 知白守黑
 * @date 2022/7/23 22:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminArticleDTO {

    /**
     * 文章id
     */
    private Integer id;

    /**
     * 文章封面
     */
    private String articleCover;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章分类
     */
    private String categoryName;

    /**
     * 文章标签
     */
    private List<TagDTO> tagDTOList;

    /**
     * 文章发布时间
     */
    private LocalDateTime createTime;

    /**
     * 文章点赞量
     */
    private Integer likeCount;

    /**
     * 文章浏览量
     */
    private Integer viewCount;

    /**
     * 文章类型(1原创 2转载 3翻译)
     */
    private String type;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 文章状态
     */
    private Integer status;
}
