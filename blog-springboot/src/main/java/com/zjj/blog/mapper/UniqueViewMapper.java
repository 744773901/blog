package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.dto.UniqueViewDTO;
import com.zjj.blog.entity.UniqueView;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/11/19 23:21
 */
public interface UniqueViewMapper extends BaseMapper<UniqueView> {

    /**
     * 获取7天内用户量统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户量
     */
    List<UniqueViewDTO> listUniqueView(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
