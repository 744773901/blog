package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.AdminPhotoDTO;
import com.zjj.blog.dto.PhotoDTO;
import com.zjj.blog.entity.Photo;
import com.zjj.blog.vo.*;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/22 21:26
 */
public interface PhotoService extends IService<Photo> {
    /**
     * 条件查询后台图片
     *
     * @param condition 条件
     * @return 后台图片列表
     */
    PageResult<AdminPhotoDTO> listPhotos(ConditionVO condition);

    /**
     * 更新逻辑删除标记
     *
     * @param deleteVO 逻辑删除信息
     */
    void updatePhotosIsDelete(DeleteVO deleteVO);

    /**
     * 批量删除图片
     *
     * @param ids 图片id列表
     */
    void batchDeletePhotos(List<Integer> ids);

    /**
     * 保存照片
     *
     * @param photoVO 照片信息
     */
    void savePhotos(PhotoVO photoVO);

    /**
     * 更新照片信息
     *
     * @param photoInfoVO 照片信息
     */
    void updatePhoto(PhotoInfoVO photoInfoVO);

    /**
     * 移动照片到相册
     *
     * @param photoVO 照片信息
     */
    void movePhotos(PhotoVO photoVO);

    /**
     * 通过相册id查询照片
     *
     * @param albumId 相册id
     * @return 照片列表
     */
    PhotoDTO listPhotosByAlbumId(Integer albumId);
}
