package com.zjj.blog.strategy.upload;

import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.strategy.UploadStrategy;
import com.zjj.blog.utils.FileUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 抽象上传策略模板
 *
 * @author 知白守黑
 * @date 2022/8/7 22:33
 */
@Component
public abstract class AbstractUploadStrategy implements UploadStrategy {

    @Override
    public String uploadFile(MultipartFile file, String path) {
        try {
            String md5 = FileUtil.getMD5(file.getInputStream());
            String extName = FileUtil.getExtName(file.getOriginalFilename());
            //重新生成文件名
            String fileName = md5 + extName;
            if (!exists(path + fileName)) {
                //文件不存在则上传
                upload(fileName, path, file.getInputStream());
            }
            return getFileAccessUrl(path + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@link Boolean}
     */
    public abstract Boolean exists(String filePath);

    /**
     * 上传文件
     *
     * @param fileName    文件名
     * @param path        路径
     * @param inputStream 字节输入流
     * @throws IOException IO异常
     */
    public abstract void upload(String fileName, String path, InputStream inputStream) throws IOException;

    /**
     * 获取文件访问地址
     *
     * @param filePath 文件路径
     * @return {@link String}
     */
    public abstract String getFileAccessUrl(String filePath);
}
