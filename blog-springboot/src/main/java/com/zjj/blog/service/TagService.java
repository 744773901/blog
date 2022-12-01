package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.TagDTO;
import com.zjj.blog.entity.Tag;
import com.zjj.blog.vo.AdminTagVO;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.TagVO;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/10 22:02
 */
public interface TagService extends IService<Tag> {

    /**
     * 条件查询文章标签
     *
     * @param condition 条件
     * @return 标签列表
     */
    List<TagDTO> listTagByOption(ConditionVO condition);

    /**
     * 查询后台标签
     *
     * @param condition 条件
     * @return 分页标签列表
     */
    PageResult<AdminTagVO> listAdminTag(ConditionVO condition);

    /**
     * 新增或修改标签
     *
     * @param tagVO 标签
     */
    void saveOrUpdateTag(TagVO tagVO);

    /**
     * 删除标签
     *
     * @param ids 标签id
     */
    void deleteTag(List<Integer> ids);

    /**
     * 查询标签列表
     *
     * @return 标签列表
     */
    PageResult<TagDTO> listTags();
}
