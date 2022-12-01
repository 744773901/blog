package com.zjj.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.zjj.blog.dto.LabelOptionDTO;
import com.zjj.blog.entity.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author 知白守黑
 * @date 2022/8/25 20:04
 */
@SpringBootTest
public class ResourceMapperTest {
    @Autowired
    private ResourceMapper resourceMapper;

    @Test
    public void test1() {
        List<Resource> resourceList = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .select(Resource::getId, Resource::getResourceName, Resource::getParentId)
                .eq(Resource::getIsAnonymous, 0));
        List<Resource> parentList = listResourceModule(resourceList);
        Map<Integer, List<Resource>> childrenListMap = listChildResource(resourceList);
        List<LabelOptionDTO> labelOptionList = parentList.stream()
                .map(item -> {
                    List<LabelOptionDTO> list = new ArrayList<>();
                    List<Resource> children = childrenListMap.get(item.getId());
                    if (CollectionUtils.isNotEmpty(children)) {
                        list = children.stream()
                                .map(resource -> LabelOptionDTO.builder()
                                        .id(resource.getId())
                                        .label(resource.getResourceName())
                                        .build())
                                .collect(Collectors.toList());
                    }
                    return LabelOptionDTO.builder()
                            .id(item.getId())
                            .label(item.getResourceName())
                            .children(list)
                            .build();
                })
                .collect(Collectors.toList());
        labelOptionList.forEach(System.out::println);
    }

    private Map<Integer, List<Resource>> listChildResource(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(item -> Objects.nonNull(item.getParentId()))
                .collect(Collectors.groupingBy(Resource::getParentId));
    }

    private List<Resource> listResourceModule(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(item -> Objects.isNull(item.getParentId()))
                .collect(Collectors.toList());
    }
}
