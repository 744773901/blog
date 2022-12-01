package com.zjj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.blog.dto.OperationLogDTO;
import com.zjj.blog.entity.OperationLog;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.PageResult;

/**
 * @author 知白守黑
 * @date 2022/11/19 19:18
 */
public interface OperationLogService extends IService<OperationLog> {

    PageResult<OperationLogDTO> listOperationLog(ConditionVO condition);
}
