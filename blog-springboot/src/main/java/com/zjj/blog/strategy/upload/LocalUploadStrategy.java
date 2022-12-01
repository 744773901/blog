package com.zjj.blog.strategy.upload;

import com.zjj.blog.handler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;

import static com.zjj.blog.constant.CommonConst.LOCAL_URL;

/**
 * @author 知白守黑
 * @date 2022/8/7 23:03
 */
@Component(value = "localUploadStrategy")
public class LocalUploadStrategy extends AbstractUploadStrategy{

    /**
     * 访问地址
     */
    @Value("${upload.local.url}")
    private String localUrl;

    /**
     * 本地路径
     */
    @Value("${upload.local.path}")
    private String localPath;

    @Override
    public Boolean exists(String filePath) {
        return new File(localPath + filePath).exists();
    }

    @Override
    public void upload(String fileName, String path, InputStream inputStream) throws IOException {
        //判断目录是否存在
        File directory = new File(localPath + path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new BusinessException("创建文件目录失败");
            }
        }
        File file = new File(localPath + path + fileName);
        if (file.createNewFile()) {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bos.close();
            bis.close();
            inputStream.close();
        }
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return LOCAL_URL + localUrl + filePath;
    }
}
