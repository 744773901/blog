package com.zjj.blog.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * 上传策略
 *
 * @author 知白守黑
 * @date 2022/8/7 22:29
 */
public interface UploadStrategy {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param path 路径
     * @return 文件访问地址
     */
    String uploadFile(MultipartFile file, String path);
}
