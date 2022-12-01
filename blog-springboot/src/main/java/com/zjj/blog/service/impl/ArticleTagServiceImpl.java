package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.entity.ArticleTag;
import com.zjj.blog.mapper.ArticleTagMapper;
import com.zjj.blog.service.ArticleTagService;
import org.springframework.stereotype.Service;

/**
 * @author 知白守黑
 * @date 2022/8/13 21:02
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}
