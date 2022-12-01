package com.zjj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.OperationLogDTO;
import com.zjj.blog.entity.OperationLog;
import com.zjj.blog.mapper.OperationLogMapper;
import com.zjj.blog.service.OperationLogService;
import com.zjj.blog.utils.BeanCopyUtil;
import com.zjj.blog.utils.PageUtil;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/11/19 19:19
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    @Override
    public PageResult<OperationLogDTO> listOperationLog(ConditionVO condition) {
        Page<OperationLog> page = new Page<>(PageUtil.getCurrent(), PageUtil.getSize());
        Page<OperationLog> resultPage = this.page(page, new LambdaQueryWrapper<OperationLog>()
                .like(StringUtils.isNotBlank(condition.getKeyword()), OperationLog::getOptModule, condition.getKeyword())
                .or()
                .like(StringUtils.isNotBlank(condition.getKeyword()), OperationLog::getOptDesc, condition.getKeyword())
                .orderByDesc(OperationLog::getId));
        List<OperationLogDTO> logList = BeanCopyUtil.copyList(resultPage.getRecords(), OperationLogDTO.class);
        return new PageResult<>(logList, ((int) resultPage.getTotal()));
    }
}
