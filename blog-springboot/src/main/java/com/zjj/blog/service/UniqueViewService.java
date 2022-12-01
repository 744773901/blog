package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.UniqueViewDTO;
import com.zjj.blog.entity.UniqueView;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/11/19 23:21
 */
public interface UniqueViewService extends IService<UniqueView> {

    /**
     * 获取7天内用户量统计
     *
     * @return 用户量
     */
    List<UniqueViewDTO> listUniqueView();
}
