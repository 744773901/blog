package com.zjj.blog.mapper;

import com.zjj.blog.dto.ArticleSearchDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 知白守黑
 * @date 2022/11/17 21:09
 */
@Repository
public interface ElasticSearchMapper extends ElasticsearchRepository<ArticleSearchDTO, Integer> {
}
