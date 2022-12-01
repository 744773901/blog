package com.zjj.blog.strategy.upload;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.zjj.blog.config.OSSPropertiesConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @author 知白守黑
 * @date 2022/8/8 18:53
 */
@Component("ossUploadStrategy")
public class OSSUploadStrategy extends AbstractUploadStrategy {

    @Resource
    private OSSPropertiesConfig ossConfig;

    @Override
    public Boolean exists(String filePath) {
        return getOSSClient().doesObjectExist(ossConfig.getBucketName(), filePath);
    }

    @Override
    public void upload(String fileName, String path, InputStream inputStream) {
        getOSSClient().putObject(ossConfig.getBucketName(), path + fileName, inputStream);
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return ossConfig.getUrl() + filePath;
    }

    private OSS getOSSClient() {
        return new OSSClientBuilder().build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
    }
}
