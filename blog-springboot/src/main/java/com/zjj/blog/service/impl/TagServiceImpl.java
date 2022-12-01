package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.TagDTO;
import com.zjj.blog.entity.ArticleTag;
import com.zjj.blog.entity.Tag;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.ArticleTagMapper;
import com.zjj.blog.mapper.TagMapper;
import com.zjj.blog.service.TagService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.vo.AdminTagVO;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.TagVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author 知白守黑
 * @date 2022/8/10 22:03
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private TagMapper tagMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;

    @Override
    public PageResult<TagDTO> listTags() {
        List<TagDTO> tagDTOList = BeanCopyUtil.copyList(tagMapper.selectList(null), TagDTO.class);
        int count = tagMapper.selectCount(null).intValue();
        return new PageResult<>(tagDTOList, count);
    }

    @Override
    public List<TagDTO> listTagByOption(ConditionVO condition) {
        //根据关键字条件查询文章标签 条件为空则按ID降序查询所有
        List<Tag> tagList = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .like(StringUtils.isNotBlank(condition.getKeyword()), Tag::getTagName, condition.getKeyword())
                .orderByDesc(Tag::getId));
        return BeanCopyUtil.copyList(tagList, TagDTO.class);
    }

    @Override
    public PageResult<AdminTagVO> listAdminTag(ConditionVO condition) {
        int count = tagMapper.selectCount(new LambdaQueryWrapper<Tag>()
                        .like(StringUtils.isNotBlank(condition.getKeyword()), Tag::getTagName, condition.getKeyword()))
                .intValue();
        if (count == 0) {
            return new PageResult<>();
        }
        List<AdminTagVO> adminTagList = tagMapper.listAdminTag(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        return new PageResult<>(adminTagList, count);
    }

    @Override
    public void saveOrUpdateTag(TagVO tagVO) {
        Tag existTag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                .select(Tag::getId)
                .eq(Tag::getTagName, tagVO.getTagName()));
        if (Objects.nonNull(existTag) && !existTag.getId().equals(tagVO.getId())) {
            throw new BusinessException("标签已存在");
        }
        Tag tag = Tag.builder()
                .id(tagVO.getId())
                .tagName(tagVO.getTagName())
                .build();
        this.saveOrUpdate(tag);
    }

    @Override
    public void deleteTag(List<Integer> ids) {
        int count = articleTagMapper.selectCount(new LambdaQueryWrapper<ArticleTag>()
                        .in(ArticleTag::getTagId, ids))
                .intValue();
        if (count > 0) {
            throw new BusinessException("删除失败,该标签下存在文章");
        }
        tagMapper.deleteBatchIds(ids);
    }
}
