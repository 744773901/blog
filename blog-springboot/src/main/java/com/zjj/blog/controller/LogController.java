package com.zjj.blog.controller;

import com.zjj.blog.service.OperationLogService;
import com.zjj.blog.vo.ConditionVO;
import com.zjj.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 知白守黑
 * @date 2022/11/19 19:34
 */
@Api(tags = "日志模块")
@RestController
public class LogController {

    @Resource
    private OperationLogService operationLogService;

    /**
     * 查看操作日志
     *
     * @param condition 查询条件
     * @return 操作日志记录
     */
    @ApiOperation(value = "查看操作日志")
    @GetMapping("/admin/operation/log")
    public Result<?> listOperationLog(ConditionVO condition) {
        return Result.ok(operationLogService.listOperationLog(condition));
    }

    /**
     * 删除操作日志
     *
     * @param ids 操作日志id集合
     * @return {@link Result}
     */
    @ApiOperation(value = "删除操作日志")
    @DeleteMapping("/admin/operation/log")
    public Result<?> deleteOperationLog(@RequestBody List<Integer> ids) {
        operationLogService.removeByIds(ids);
        return Result.ok();
    }
}
