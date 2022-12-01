package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjj.blog.dto.AdminPhotoAlbumDTO;
import com.zjj.blog.entity.PhotoAlbum;
import com.zjj.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/22 17:11
 */
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbum> {

    /**
     * 查询后台相册
     *
     * @param current   页码
     * @param size      大小
     * @param condition 条件
     * @return 后台相册列表
     */
    List<AdminPhotoAlbumDTO> selectAdminPhotoAlbumList(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO condition);
}
