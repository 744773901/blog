package com.zjj.blog;

import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjj.blog.entity.Resource;
import com.zjj.blog.entity.RoleResource;
import com.zjj.blog.mapper.ResourceMapper;
import com.zjj.blog.mapper.RoleResourceMapper;
import com.zjj.blog.service.ResourceService;
import com.zjj.blog.service.RoleResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogSpringbootApplicationTests {

    @Autowired
    ResourceMapper resourceMapper;
    @Autowired
    ResourceService resourceService;
    @Autowired
    RoleResourceMapper roleResourceMapper;
    @Autowired
    RoleResourceService roleResourceService;

    @Test
    void contextLoads() {
        Set<Class<?>> classSet = ClassUtil.scanPackage("com.zjj.blog.controller");
        List<Resource> moduleResources = classSet.stream().map(clazz -> {
            Api api = clazz.getAnnotation(Api.class);
            return Resource.builder()
                    .resourceName(api.tags()[0])
                    .build();
        }).collect(Collectors.toList());
        resourceService.saveBatch(moduleResources);
        List<Resource> interfaceList = new ArrayList<>();
        classSet.forEach(clazz -> {
            Api api = clazz.getAnnotation(Api.class);
            Method[] methods = clazz.getDeclaredMethods();
            Resource resource;
            for (Method method : methods) {
                ApiOperation apiOperation = method.getDeclaredAnnotation(ApiOperation.class);
                resource = Resource.builder()
                        .resourceName(apiOperation.value())
                        .build();
                GetMapping getMapping = method.getDeclaredAnnotation(GetMapping.class);
                if (Objects.nonNull(getMapping)) {
                    resource.setUrl(getMapping.value()[0].replaceAll("\\{[^}]+}", "*"));
                    resource.setRequestMethod("GET");
                }
                PutMapping putMapping = method.getDeclaredAnnotation(PutMapping.class);
                if (Objects.nonNull(putMapping)) {
                    resource.setUrl(putMapping.value()[0].replaceAll("\\{[^}]+}", "*"));
                    resource.setRequestMethod("PUT");
                }
                PostMapping postMapping = method.getDeclaredAnnotation(PostMapping.class);
                if (Objects.nonNull(postMapping)) {
                    resource.setUrl(postMapping.value()[0].replaceAll("\\{[^}]+}", "*"));
                    resource.setRequestMethod("POST");
                }
                DeleteMapping deleteMapping = method.getDeclaredAnnotation(DeleteMapping.class);
                if (Objects.nonNull(deleteMapping)) {
                    resource.setUrl(deleteMapping.value()[0].replaceAll("\\{[^}]+}", "*"));
                    resource.setRequestMethod("DELETE");
                }
                for (Resource moduleResource : moduleResources) {
                    if (moduleResource.getResourceName().equals(api.tags()[0])) {
                        resource.setParentId(moduleResource.getId());
                    }
                }
                interfaceList.add(resource);
            }
        });
        resourceService.saveBatch(interfaceList);
        moduleResources.forEach(System.out::println);
        System.out.println("============================");
        interfaceList.forEach(System.out::println);

    }

    @Test
    public void test() {
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getIsAnonymous, 0)
                .eq(Resource::getRequestMethod, "GET")
                .isNotNull(Resource::getParentId));
        resources.forEach(System.out::println);
        List<RoleResource> roleResources = resources.stream()
                .map(resource -> RoleResource.builder()
                        .roleId(3)
                        .resourceId(resource.getId())
                        .build())
                .collect(Collectors.toList());
        System.out.println("===============");
        roleResources.forEach(System.out::println);
        roleResourceService.saveBatch(roleResources);
    }

    @Test
    public void test2() {
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getIsAnonymous, 0)
                .eq(Resource::getRequestMethod, "GET")
                .isNotNull(Resource::getParentId));
        List<RoleResource> roleResourceList = resources.stream()
                .filter(resource -> resource.getId() != 22 || resource.getId() != 57 || resource.getId() != 110)
                .map(resource -> RoleResource.builder()
                        .roleId(3)
                        .resourceId(resource.getId())
                        .build())
                .collect(Collectors.toList());
        roleResourceList.forEach(System.out::println);
    }

    public static void main(String[] args) {
        Set<Class<?>> classSet = ClassUtil.scanPackage("com.zjj.blog.controller");
        List<com.zjj.blog.entity.Resource> moduleList = new ArrayList<>();
        List<com.zjj.blog.entity.Resource> interfaceList = new ArrayList<>();
        for (Class<?> clazz : classSet) {
            Api api = clazz.getAnnotation(Api.class);
            System.out.println(Arrays.toString(api.tags()));
            moduleList.add(Resource.builder()
                    .resourceName(api.tags()[0])
                    .build());
            Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {
                ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
                System.out.println(apiOperation.value());
            });
        }
        moduleList.forEach(System.out::println);
    }

}
