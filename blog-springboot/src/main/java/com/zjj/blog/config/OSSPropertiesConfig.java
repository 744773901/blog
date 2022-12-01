package com.zjj.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 知白守黑
 * @date 2022/8/8 19:12
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload.oss")
public class OSSPropertiesConfig {

    /**
     * 自定义域名
     */
    private String url;

    /**
     * 地域节点
     */
    private String endpoint;

    /**
     * Bucket名称
     */
    private String bucketName;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;
}
