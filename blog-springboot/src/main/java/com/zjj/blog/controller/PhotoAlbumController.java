package com.zjj.blog.controller;

import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.AdminPhotoAlbumDTO;
import com.zjj.blog.dto.PhotoAlbumDTO;
import com.zjj.blog.enums.FilePathEnum;
import com.zjj.blog.service.PhotoAlbumService;
import com.zjj.blog.strategy.context.UploadStrategyContext;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import com.zjj.blog.vo.PhotoAlbumVO;
import com.zjj.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/22 20:57
 */
@Api(tags = "相册模块")
@RestController
public class PhotoAlbumController {

    @Resource
    private PhotoAlbumService photoAlbumService;
    @Resource
    private UploadStrategyContext uploadStrategyContext;

    /**
     * 上传相册封面
     *
     * @param file 文件
     * @return 图片访问地址
     */
    @ApiOperation(value = "上传相册封面")
    @PostMapping("/admin/photo/album/cover")
    public Result<?> uploadPhotoAlbumCover(MultipartFile file) {
        return Result.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.PHOTO.getPath()));
    }

    /**
     * 查询相册列表
     *
     * @return {@link Result<PhotoAlbumDTO>} 相册列表
     */
    @ApiOperation(value = "查询相册列表")
    @GetMapping("/photo/albums")
    public Result<List<PhotoAlbumDTO>> listPhotoAlbums() {
        return Result.ok(photoAlbumService.listPhotoAlbums());
    }

    /**
     * 查询后台相册
     *
     * @param condition 条件
     * @return {@link Result}
     */
    @ApiOperation(value = "查询后台相册")
    @GetMapping("/admin/photo/albums")
    public Result<PageResult<AdminPhotoAlbumDTO>> listAdminPhotoAlbum(ConditionVO condition) {
        return Result.ok(photoAlbumService.listAdminPhotoAlbum(condition));
    }

    /**
     * 查询所有后台相册信息
     *
     * @return 后台相册列表
     */
    @ApiOperation(value = "查询所有后台相册信息")
    @GetMapping("/admin/photo/albums/info")
    public Result<List<PhotoAlbumDTO>> listAllAdminPhotoAlbumInfo() {
        return Result.ok(photoAlbumService.listAllAdminPhotoAlbumInfo());
    }

    /**
     * 根据id查询后台相册
     *
     * @param id 相册id
     * @return {@link Result<AdminPhotoAlbumDTO>}
     */
    @ApiOperation(value = "根据id查询后台相册")
    @GetMapping("/admin/photo/album/{id}")
    public Result<AdminPhotoAlbumDTO> getAdminPhotoAlbumById(@PathVariable("id") Integer id) {
        return Result.ok(photoAlbumService.getAdminPhotoAlbumById(id));
    }

    /**
     * 新增或修改相册
     *
     * @param photoAlbumVO 相册信息
     * @return {@link Result}
     */
    @OperateLog(type = SAVE_OR_UPDATE)
    @ApiOperation(value = "新增或修改相册")
    @PostMapping("/admin/photo/albums")
    public Result<?> saveOrUpdatePhotoAlbum(@Validated @RequestBody PhotoAlbumVO photoAlbumVO) {
        photoAlbumService.saveOrUpdatePhotoAlbum(photoAlbumVO);
        return Result.ok();
    }

    /**
     * 根据id删除相册
     *
     * @param id 相册id
     * @return {@link Result}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "根据id删除相册")
    @DeleteMapping("/admin/photo/album/{id}")
    public Result<?> deletePhotoAlbumById(@PathVariable("id") Integer id) {
        photoAlbumService.deletePhotoAlbumById(id);
        return Result.ok();
    }
}
