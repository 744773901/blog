package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.entity.Page;
import com.zjj.blog.vo.PageVO;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/24 14:58
 */
public interface PageService extends IService<Page> {
    /**
     * 查询所有页面
     *
     * @return 页面列表
     */
    List<PageVO> listPages();

    /**
     * 新增或修改页面
     *
     * @param pageVO 页面信息
     */
    void saveOrUpdatePage(PageVO pageVO);

    /**
     * 根据id删除页面
     *
     * @param id 页面id
     */
    void deletePage(Integer id);
}
