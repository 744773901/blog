package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.AdminFriendLinkDTO;
import com.zjj.blog.dto.FriendLinkDTO;
import com.zjj.blog.entity.FriendLink;
import com.zjj.blog.mapper.FriendLinkMapper;
import com.zjj.blog.service.FriendLinkService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.FriendLinkVO;
import com.zjj.blog.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/8/24 16:53
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {
    @Resource
    private FriendLinkMapper friendLinkMapper;

    @Override
    public List<FriendLinkDTO> listFriendLinks() {
        return BeanCopyUtil.copyList(friendLinkMapper.selectList(null), FriendLinkDTO.class);
    }

    @Override
    public PageResult<AdminFriendLinkDTO> listAdminFriendLink(ConditionVO condition) {
        Page<FriendLink> page = new Page<>(PageUtil.getCurrent(), PageUtil.getSize());
        Page<FriendLink> friendLinkPage = friendLinkMapper.selectPage(page, new LambdaQueryWrapper<FriendLink>()
                .like(StringUtils.isNotBlank(condition.getKeyword()), FriendLink::getLinkName, condition.getKeyword()));
        List<AdminFriendLinkDTO> adminFriendLinkList = BeanCopyUtil.copyList(friendLinkPage.getRecords(), AdminFriendLinkDTO.class);
        return new PageResult<>(adminFriendLinkList, ((int) friendLinkPage.getTotal()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO) {
        this.saveOrUpdate(BeanCopyUtil.copyObject(friendLinkVO, FriendLink.class));
    }
}
