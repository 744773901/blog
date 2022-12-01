package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.AdminPhotoDTO;
import com.zjj.blog.dto.PhotoDTO;
import com.zjj.blog.service.PhotoService;
import com.zjj.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/23 16:18
 */
@Api(tags = "图片模块")
@RestController
public class PhotoController {
    @Resource
    private PhotoService photoService;

    /**
     * 根据相册id查看照片列表
     *
     * @param albumId 相册id
     * @return {@link Result<PhotoDTO>} 照片列表
     */
    @ApiOperation(value = "根据相册id查看照片列表")
    @GetMapping("/album/{albumId}/photos")
    public Result<PhotoDTO> listPhotosByAlbumId(@PathVariable("albumId") Integer albumId) {
        return Result.ok(photoService.listPhotosByAlbumId(albumId));
    }

    /**
     * 条件查询后台图片
     *
     * @param condition 条件
     * @return {@link Result}
     */
    @ApiOperation(value = "条件查询后台图片")
    @GetMapping("/admin/photos")
    public Result<PageResult<AdminPhotoDTO>> listPhotos(ConditionVO condition) {
        return Result.ok(photoService.listPhotos(condition));
    }

    /**
     * 更新逻辑删除标记
     *
     * @param deleteVO 逻辑删除信息
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "更新逻辑删除标记")
    @PutMapping("/admin/photos/delete")
    public Result<?> updatePhotosIsDelete(@Validated @RequestBody DeleteVO deleteVO) {
        photoService.updatePhotosIsDelete(deleteVO);
        return Result.ok();
    }

    /**
     * 保存照片
     *
     * @param photoVO 照片信息
     * @return {@link Result}
     */
    @OperateLog(type = SAVE)
    @ApiOperation(value = "保存照片")
    @PostMapping("/admin/photos")
    public Result<?> savePhotos(@Validated @RequestBody PhotoVO photoVO) {
        photoService.savePhotos(photoVO);
        return Result.ok();
    }

    /**
     * 更新照片信息
     *
     * @param photoInfoVO 照片信息
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "保存照片")
    @PutMapping("/admin/photos")
    public Result<?> updatePhoto(@Validated @RequestBody PhotoInfoVO photoInfoVO) {
        photoService.updatePhoto(photoInfoVO);
        return Result.ok();
    }

    /**
     * 移动照片到相册
     *
     * @param photoVO 照片信息
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "移动照片到相册")
    @PutMapping("/admin/photos/move")
    public Result<?> movePhotos(@Validated @RequestBody PhotoVO photoVO) {
        photoService.movePhotos(photoVO);
        return Result.ok();
    }

    /**
     * 批量删除图片
     *
     * @param ids 图片id列表
     * @return {@link Result}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "批量删除图片")
    @DeleteMapping("/admin/photos")
    public Result<?> batchDeletePhotos(@RequestBody List<Integer> ids) {
        photoService.batchDeletePhotos(ids);
        return Result.ok();
    }
}
