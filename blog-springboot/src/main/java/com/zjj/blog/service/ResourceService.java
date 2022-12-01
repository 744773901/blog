package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.LabelOptionDTO;
import com.zjj.blog.dto.ResourceDTO;
import com.zjj.blog.entity.Resource;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.ResourceVO;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/25 20:02
 */
public interface ResourceService extends IService<Resource> {

    /**
     * 查看资源选项
     *
     * @return 资源选项
     */
    List<LabelOptionDTO> listResourceOption();

    /**
     * 查询资源列表
     *
     * @param condition 条件
     * @return 资源列表
     */
    List<ResourceDTO> listResources(ConditionVO condition);

    /**
     * 根据id删除资源
     *
     * @param id 资源id
     */
    void deleteResourceById(Integer id);

    /**
     * 新增或修改资源
     *
     * @param resourceVO 资源信息
     */
    void saveOrUpdateResource(ResourceVO resourceVO);
}
