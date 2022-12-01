package com.zjj.blog.config;

import com.zjj.blog.handler.PaginationHandlerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * WebMvc配置
 *
 * @author 知白守黑
 * @date 2022/7/14 18:06
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.local.url}")
    private String localUrl;

    @Value("${upload.local.path}")
    private String localPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowedOriginPatterns("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(localUrl + "**").addResourceLocations("file:" + localPath);
//        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PaginationHandlerInterceptor());
    }
}
