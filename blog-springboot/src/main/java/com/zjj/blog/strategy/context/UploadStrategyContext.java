package com.zjj.blog.strategy.context;

import com.zjj.blog.enums.UploadModeEnum;
import com.zjj.blog.strategy.UploadStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 知白守黑
 * @date 2022/8/7 23:33
 */
@Component
public class UploadStrategyContext {

    @Value("${upload.mode}")
    private String mode;

    @Resource
    private Map<String, UploadStrategy> uploadStrategyMap;

    /**
     * 执行上传文件策略
     * @param file 文件
     * @param path 路径
     * @return 文件访问地址
     */
    public String executeUploadStrategy(MultipartFile file, String path) {
        return uploadStrategyMap.get(UploadModeEnum.getStrategy(mode)).uploadFile(file, path);
    }
}
