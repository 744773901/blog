package com.zjj.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.entity.Page;
import com.zjj.blog.mapper.PageMapper;
import com.zjj.blog.service.PageService;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.zjj.blog.constant.RedisConst.PAGE_COVER;

/**
 * @author 知白守黑
 * @date 2022/8/24 14:59
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements PageService {
    @Resource
    private PageMapper pageMapper;
    @Resource
    private RedisService redisService;

    @Override
    public List<PageVO> listPages() {
        List<PageVO> pageVOList;
        Object pageList = redisService.get(PAGE_COVER);
        if (Objects.nonNull(pageList)) {
            pageVOList = JSON.parseArray(pageList.toString(), PageVO.class);
        } else {
            pageVOList = BeanCopyUtil.copyList(pageMapper.selectList(null), PageVO.class);
            redisService.set(PAGE_COVER, pageVOList);
        }
        return pageVOList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdatePage(PageVO pageVO) {
        this.saveOrUpdate(BeanCopyUtil.copyObject(pageVO, Page.class));
        redisService.del(PAGE_COVER);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletePage(Integer id) {
        pageMapper.deleteById(id);
        redisService.del(PAGE_COVER);
    }
}
