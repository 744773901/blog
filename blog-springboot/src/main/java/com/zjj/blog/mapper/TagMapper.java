package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.entity.Tag;
import com.zjj.blog.vo.AdminTagVO;
import com.zjj.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/10 20:48
 */
public interface TagMapper extends BaseMapper<Tag> {

    List<String> listTagNameByArticleId(@Param("id") Integer id);

    List<AdminTagVO> listAdminTag(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);
}
