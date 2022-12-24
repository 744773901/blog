package com.zjj.blog.aspect;

import com.zjj.blog.annotation.AccessRateLimit;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.utils.HTMLUtil;
import com.zjj.blog.utils.IPUtil;
import com.zjj.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class AccessRateLimitAspect {

    @Resource
    private RedisService redisService;

    @Pointcut("@annotation(com.zjj.blog.annotation.AccessRateLimit)")
    public void pointCut() {

    }

    @Around(value = "pointCut()")
    public Object limit(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = Objects.requireNonNull(servletRequestAttributes.getResponse());
        MethodSignature methodSignature = ((MethodSignature) joinPoint.getSignature());
        Method method = methodSignature.getMethod();
        AccessRateLimit accessRateLimit = method.getAnnotation(AccessRateLimit.class);
        Object res = null;
        if (Objects.nonNull(accessRateLimit)) {
            int second = accessRateLimit.second();
            int counter = accessRateLimit.counter();
            String key = IPUtil.getIpAddress(request) + ":" + method.getName();
            try {
                Long count = redisService.incrExpire(key, second);
                if (count <= counter) {
                    res = joinPoint.proceed();
                } else {
                    log.warn("[{}]请求次数在{}秒内超过{}次", key, second, counter);
                    HTMLUtil.render(response, Result.fail("服务繁忙，请稍后再试"));
                }
            } catch (Throwable e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return res;
    }
}