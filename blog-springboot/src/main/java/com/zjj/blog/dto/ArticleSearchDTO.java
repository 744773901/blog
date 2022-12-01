package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 搜索文章
 *
 * @author 知白守黑
 * @date 2022/11/15 14:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "article")
public class ArticleSearchDTO {

    /**
     * 文章ID
     */
    @Id
    private Integer id;

    /**
     * 文章标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String articleTitle;

    /**
     * 文章内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String articleContent;

    /**
     * 逻辑删除
     */
    @Field(type = FieldType.Integer)
    private Integer isDelete;

    /**
     * 文章状态(1公开 2私密 3评论可见)
     */
    @Field(type = FieldType.Integer)
    private Integer status;
}
