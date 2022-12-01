package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.AdminPhotoAlbumDTO;
import com.zjj.blog.dto.PhotoAlbumDTO;
import com.zjj.blog.entity.PhotoAlbum;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.PhotoAlbumVO;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/22 20:56
 */
public interface PhotoAlbumService extends IService<PhotoAlbum> {

    /**
     * 查询后台相册列表
     *
     * @param condition 条件
     * @return 后台相册列表
     */
    PageResult<AdminPhotoAlbumDTO> listAdminPhotoAlbum(ConditionVO condition);

    /**
     * 新增或修改相册
     *
     * @param photoAlbumVO 相册信息
     */
    void saveOrUpdatePhotoAlbum(PhotoAlbumVO photoAlbumVO);

    /**
     * 根据id删除相册
     *
     * @param id 相册id
     */
    void deletePhotoAlbumById(Integer id);

    /**
     * 查询所有后台相册
     *
     * @return 后台相册列表
     */
    List<PhotoAlbumDTO> listAllAdminPhotoAlbumInfo();

    /**
     * 根据id查询后台相册
     *
     * @param id 相册id
     * @return {@link AdminPhotoAlbumDTO}
     */
    AdminPhotoAlbumDTO getAdminPhotoAlbumById(Integer id);

    /**
     * 查询相册列表
     *
     * @return 相册列表
     */
    List<PhotoAlbumDTO> listPhotoAlbums();
}
