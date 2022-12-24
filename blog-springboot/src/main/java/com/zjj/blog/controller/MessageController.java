package com.zjj.blog.controller;

import com.zjj.blog.annotation.AccessRateLimit;
import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.dto.MessageDTO;
import com.zjj.blog.service.MessageService;
import com.zjj.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zjj.blog.constant.OperateTypeConst.*;

/**
 * @author 知白守黑
 * @date 2022/8/16 16:21
 */
@Api(tags = "留言模块")
@RestController
public class MessageController {

    @Resource
    private MessageService messageService;

    /**
     * 查询留言
     *
     * @return {@link Result<MessageDTO>} 留言列表
     */
    @ApiOperation(value = "查询留言")
    @GetMapping("/messages")
    public Result<List<MessageDTO>> listMessages() {
        return Result.ok(messageService.listMessages());
    }

    /**
     * 添加留言
     *
     * @param messageVO 留言信息
     * @return {@link Result<>}
     */
    @AccessRateLimit(second = 60, counter = 3)
    @ApiOperation(value = "添加留言")
    @PostMapping("/messages")
    public Result<?> saveMessage(@Validated @RequestBody MessageVO messageVO) {
        messageService.saveMessage(messageVO);
        return Result.ok();
    }

    /**
     * 查询后台留言
     *
     * @param condition 条件
     * @return {@link Result}
     */
    @ApiOperation(value = "查询后台留言")
    @GetMapping("/admin/messages")
    public Result<PageResult<AdminMessageVO>> listAdminMessage(ConditionVO condition) {
        return Result.ok(messageService.listAdminMessage(condition));
    }

    /**
     * 审核留言
     *
     * @param reviewVO 审核信息
     * @return {@link Result}
     */
    @OperateLog(type = UPDATE)
    @ApiOperation(value = "审核留言")
    @PutMapping("/admin/messages/review")
    public Result<?> updateMessageReview(@Validated @RequestBody ReviewVO reviewVO) {
        messageService.updateMessageReview(reviewVO);
        return Result.ok();
    }

    /**
     * 删除留言
     *
     * @param ids ID列表
     * @return {@link Result}
     */
    @OperateLog(type = DELETE)
    @ApiOperation(value = "删除留言")
    @DeleteMapping("/admin/messages")
    public Result<?> deleteMessage(@RequestBody List<Integer> ids) {
        messageService.removeByIds(ids);
        return Result.ok();
    }
}
