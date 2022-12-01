package com.zjj.blog.handler.exception;

import com.zjj.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

import static com.zjj.blog.enums.HttpStatusEnum.*;

/**
 * 全局异常处理
 *
 * @author 知白守黑
 * @date 2022/7/17 20:17
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    /**
     * 处理业务异常
     *
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> exceptionHandler(BusinessException e) {
        log.error(e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     *
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> exceptionHandler(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.error(message);
        return Result.fail(VALID_ERROR.getCode(), message);
    }

    /**
     * 处理系统异常
     *
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(Exception.class)
    public Result<?> exceptionHandler(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        if (e instanceof BadCredentialsException || e instanceof InternalAuthenticationServiceException) {
            return Result.fail(AUTHENTICATION_FAILED.getCode(), AUTHENTICATION_FAILED.getMessage());
        }
        return Result.fail(SYSTEM_ERROR.getCode(), SYSTEM_ERROR.getMessage());
    }
}
