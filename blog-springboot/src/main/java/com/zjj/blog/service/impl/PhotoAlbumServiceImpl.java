package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.AdminPhotoAlbumDTO;
import com.zjj.blog.dto.PhotoAlbumDTO;
import com.zjj.blog.entity.Photo;
import com.zjj.blog.entity.PhotoAlbum;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.PhotoAlbumMapper;
import com.zjj.blog.mapper.PhotoMapper;
import com.zjj.blog.service.PhotoAlbumService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.PhotoAlbumVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.zjj.blog.constant.CommonConst.FALSE;
import static com.zjj.blog.constant.CommonConst.TRUE;
import static com.zjj.blog.enums.StatusEnum.PUBLIC;

/**
 * @author 知白守黑
 * @date 2022/8/22 20:56
 */
@Service
public class PhotoAlbumServiceImpl extends ServiceImpl<PhotoAlbumMapper, PhotoAlbum> implements PhotoAlbumService {
    @Resource
    private PhotoAlbumMapper photoAlbumMapper;
    @Resource
    private PhotoMapper photoMapper;

    @Override
    public PageResult<AdminPhotoAlbumDTO> listAdminPhotoAlbum(ConditionVO condition) {
        int count = photoAlbumMapper.selectCount(new LambdaQueryWrapper<PhotoAlbum>()
                .like(StringUtils.isNotBlank(condition.getKeyword()), PhotoAlbum::getAlbumName, condition.getKeyword())
                .eq(PhotoAlbum::getIsDelete, FALSE)).intValue();
        if (count == 0) {
            return new PageResult<>();
        }
        List<AdminPhotoAlbumDTO> adminPhotoAlbumList = photoAlbumMapper.selectAdminPhotoAlbumList(PageUtil.getLimitCurrent(), PageUtil.getSize(), condition);
        return new PageResult<>(adminPhotoAlbumList, count);
    }

    @Override
    public List<PhotoAlbumDTO> listAllAdminPhotoAlbumInfo() {
        List<PhotoAlbum> photoAlbums = photoAlbumMapper.selectList(new LambdaQueryWrapper<PhotoAlbum>()
                .eq(PhotoAlbum::getIsDelete, FALSE));
        return BeanCopyUtil.copyList(photoAlbums, PhotoAlbumDTO.class);
    }

    @Override
    public List<PhotoAlbumDTO> listPhotoAlbums() {
        // 查询相册列表
        List<PhotoAlbum> photoAlbumList = photoAlbumMapper.selectList(new LambdaQueryWrapper<PhotoAlbum>()
                .eq(PhotoAlbum::getStatus, PUBLIC.getStatus())
                .eq(PhotoAlbum::getIsDelete, FALSE)
                .orderByDesc(PhotoAlbum::getId));
        return BeanCopyUtil.copyList(photoAlbumList, PhotoAlbumDTO.class);
    }

    @Override
    public AdminPhotoAlbumDTO getAdminPhotoAlbumById(Integer id) {
        PhotoAlbum photoAlbum = photoAlbumMapper.selectById(id);
        Long count = photoMapper.selectCount(new LambdaQueryWrapper<Photo>()
                .eq(Photo::getAlbumId, id)
                .eq(Photo::getIsDelete, FALSE));
        AdminPhotoAlbumDTO adminPhotoAlbum = BeanCopyUtil.copyObject(photoAlbum, AdminPhotoAlbumDTO.class);
        adminPhotoAlbum.setPhotoCount(count.intValue());
        return adminPhotoAlbum;
    }

    @Override
    public void saveOrUpdatePhotoAlbum(PhotoAlbumVO photoAlbumVO) {
        PhotoAlbum photoAlbum = photoAlbumMapper.selectOne(new LambdaQueryWrapper<PhotoAlbum>()
                .select(PhotoAlbum::getId)
                .eq(PhotoAlbum::getAlbumName, photoAlbumVO.getAlbumName()));
        if (Objects.nonNull(photoAlbum) && !photoAlbum.getId().equals(photoAlbumVO.getId())) {
            throw new BusinessException("相册名称重复");
        }
        PhotoAlbum album = BeanCopyUtil.copyObject(photoAlbumVO, PhotoAlbum.class);
        this.saveOrUpdate(album);
    }

    @Override
    public void deletePhotoAlbumById(Integer id) {
        int count = photoMapper.selectCount(new LambdaQueryWrapper<Photo>()
                .eq(Photo::getAlbumId, id)).intValue();
        if (count > 0) {
            photoAlbumMapper.updateById(PhotoAlbum.builder()
                    .id(id)
                    .isDelete(TRUE)
                    .build());
            photoMapper.update(new Photo(), new LambdaUpdateWrapper<Photo>()
                    .set(Photo::getIsDelete, TRUE)
                    .eq(Photo::getAlbumId, id));
        } else {
            photoAlbumMapper.deleteById(id);
        }
    }
}
