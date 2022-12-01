package com.zjj.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 知白守黑
 * @date 2022/10/18 20:42
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class SecurityProperties {

    /**
     * 请求头 Authorization
     */
    private String header;

    /**
     * token前缀 Bearer
     */
    private String tokenStartWith;

    /**
     * token过期时间 单位/ms
     */
    private Long expiredMillisecond;
}
