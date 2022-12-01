package com.zjj.blog.mapper;

import com.zjj.blog.dto.ArticleSearchDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author 知白守黑
 * @date 2022/11/17 21:09
 */
//防止重复注入
@NoRepositoryBean
public interface ElasticSearchMapper extends ElasticsearchRepository<ArticleSearchDTO, Integer> {
}
