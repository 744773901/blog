package com.zjj.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j配置
 *
 * @author 知白守黑
 * @date 2022/7/14 18:10
 */
@Configuration
@EnableSwagger2
public class Knife4jConfig {
    @Bean
    public Docket restApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("Knife4j接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zjj.blog.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("博客接口文档")
                .description("springboot+vue开发的博客系统")
                .contact(new Contact("知白守黑", "https://github.com", "744773901@qq.com"))
                .termsOfServiceUrl("https://www.theblindlight.com/api")
                .version("1.0")
                .build();
    }
}
