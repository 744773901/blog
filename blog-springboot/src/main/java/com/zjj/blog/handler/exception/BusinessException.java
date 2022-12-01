package com.zjj.blog.handler.exception;

import com.zjj.blog.enums.HttpStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author 知白守黑
 * @date 2022/7/17 20:09
 */
@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误信息
     */
    private String message;

    public BusinessException(HttpStatusEnum httpStatusEnum) {
        this.code = httpStatusEnum.getCode();
        this.message = httpStatusEnum.getMessage();
    }

    public BusinessException(String message) {
        this.message = message;
    }
}
