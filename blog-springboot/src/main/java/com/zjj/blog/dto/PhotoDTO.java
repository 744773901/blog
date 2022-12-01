package com.zjj.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/29 22:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
    /**
     * 相册封面
     */
    private String photoAlbumCover;

    /**
     * 相册名
     */
    private String photoAlbumName;

    /**
     * 照片列表
     */
    private List<String> photoList;
}
