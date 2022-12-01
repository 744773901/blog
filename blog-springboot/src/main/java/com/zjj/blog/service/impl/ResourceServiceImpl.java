package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.LabelOptionDTO;
import com.zjj.blog.dto.ResourceDTO;
import com.zjj.blog.entity.Resource;
import com.zjj.blog.entity.RoleResource;
import com.zjj.blog.handler.exception.BusinessException;
import com.zjj.blog.mapper.ResourceMapper;
import com.zjj.blog.mapper.RoleResourceMapper;
import com.zjj.blog.security.FilterInvocationSecurityMetadataSourceImpl;
import com.zjj.blog.service.ResourceService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.ResourceVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 知白守黑
 * @date 2022/8/25 20:02
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @javax.annotation.Resource
    private ResourceMapper resourceMapper;
    @javax.annotation.Resource
    private RoleResourceMapper roleResourceMapper;
    @javax.annotation.Resource
    private FilterInvocationSecurityMetadataSourceImpl filterInvocationSecurityMetadataSource;

    @Override
    public List<LabelOptionDTO> listResourceOption() {
        List<Resource> resourceList = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .select(Resource::getId, Resource::getResourceName, Resource::getParentId)
                .eq(Resource::getIsAnonymous, 0));
        List<Resource> parentList = listResourceModule(resourceList);
        Map<Integer, List<Resource>> childrenListMap = listChildResource(resourceList);
        return parentList.stream()
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
    }

    /**
     * 获取模块下的所有资源
     *
     * @param resourceList 资源列表
     * @return 模块资源
     */
    private Map<Integer, List<Resource>> listChildResource(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(item -> Objects.nonNull(item.getParentId()))
                .collect(Collectors.groupingBy(Resource::getParentId));
    }

    /**
     * 获取资源模块
     *
     * @param resourceList 资源列表
     * @return 资源模块
     */
    private List<Resource> listResourceModule(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(item -> Objects.isNull(item.getParentId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceDTO> listResources(ConditionVO condition) {
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .like(StringUtils.isNotBlank(condition.getKeyword()), Resource::getResourceName, condition.getKeyword()));
        List<Resource> parentList = listResourceModule(resources);
        Map<Integer, List<Resource>> childrenListMap = listChildResource(resources);
        List<ResourceDTO> resourceDTOList = parentList.stream().map(item -> {
            ResourceDTO resourceDTO = BeanCopyUtil.copyObject(item, ResourceDTO.class);
            List<ResourceDTO> childrenList = BeanCopyUtil.copyList(childrenListMap.get(item.getId()), ResourceDTO.class);
            resourceDTO.setChildren(childrenList);
            childrenListMap.remove(item.getId());
            return resourceDTO;
        }).collect(Collectors.toList());
        // 若还有资源未取出则拼接
        if (CollectionUtils.isNotEmpty(childrenListMap)) {
            List<Resource> childrenList = new ArrayList<>();
            childrenListMap.values().forEach(childrenList::addAll);
            List<ResourceDTO> childrenDTOList = childrenList.stream()
                    .map(item -> BeanCopyUtil.copyObject(item, ResourceDTO.class))
                    .collect(Collectors.toList());
            resourceDTOList.addAll(childrenDTOList);
        }
        return resourceDTOList;
    }

    @Override
    public void deleteResourceById(Integer id) {
        // 查询是否有角色关联
        int count = roleResourceMapper.selectCount(new LambdaQueryWrapper<RoleResource>()
                .eq(RoleResource::getResourceId, id)).intValue();
        if (count > 0) {
            throw new BusinessException("该资源下有关联角色");
        }
        // 删除子资源
        List<Integer> resourceIdList = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                        .select(Resource::getId).
                        eq(Resource::getParentId, id))
                .stream()
                .map(Resource::getId)
                .collect(Collectors.toList());
        resourceIdList.add(id);
        resourceMapper.deleteBatchIds(resourceIdList);
    }

    @Override
    public void saveOrUpdateResource(ResourceVO resourceVO) {
        // 更新资源信息
        Resource resource = BeanCopyUtil.copyObject(resourceVO, Resource.class);
        this.saveOrUpdate(resource);
        // 重新加载角色资源信息
        filterInvocationSecurityMetadataSource.clearDataSource();
    }
}
