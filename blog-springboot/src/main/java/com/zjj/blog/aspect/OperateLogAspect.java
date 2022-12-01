package com.zjj.blog.aspect;

import com.alibaba.fastjson2.JSON;
import com.zjj.blog.annotation.OperateLog;
import com.zjj.blog.entity.OperationLog;
import com.zjj.blog.mapper.OperationLogMapper;
import com.zjj.blog.utils.IPUtil;
import com.zjj.blog.utils.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 操作日志切面
 *
 * @author 知白守黑
 * @date 2022/11/18 20:51
 */
@Aspect
@Component
public class OperateLogAspect {

    @Resource
    private OperationLogMapper operationLogMapper;

    /**
     * 设置切入点 在加上注解的位置切入代码
     */
    @Pointcut("@annotation(com.zjj.blog.annotation.OperateLog)")
    public void pointCut() {
    }

    /**
     * 无异常返回通知
     *
     * @param joinPoint 连接点
     * @param object    被切入方法的返回值
     */
    @AfterReturning(pointcut = "pointCut()", returning = "object")
    @SuppressWarnings("unchecked")
    public void saveOperationLog(JoinPoint joinPoint, Object object) {
        //获取当前线程绑定请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
        //获取被增强方法的方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取被增强方法
        Method method = signature.getMethod();
        //获取控制器的模块信息
        Api api = (Api) signature.getDeclaringType().getAnnotation(Api.class);
        //获取控制器的操作描述信息
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        //获取控制器的操作日志
        OperateLog operateLog = method.getAnnotation(OperateLog.class);
        //获取完整方法名
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        //获取请求方式
        String requestMethod = request.getMethod();
        //获取请求URL
        String url = request.getRequestURI();
        //获取请求参数
        String args = JSON.toJSONString(joinPoint.getArgs());
        //获取返回结果
        String result = JSON.toJSONString(object);
        String ipAddress = IPUtil.getIpAddress(request);
        String ipSource = IPUtil.getIpSource(ipAddress);
        OperationLog operationLog = OperationLog.builder()
                .optModule(api.tags()[0])
                .optDesc(apiOperation.value())
                .optType(operateLog.type())
                .optMethod(methodName)
                .requestMethod(requestMethod)
                .optUrl(url)
                .requestParam(args)
                .responseData(result)
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .userId(UserUtil.getLoginUser().getId())
                .nickname(UserUtil.getLoginUser().getNickname())
                .build();
        operationLogMapper.insert(operationLog);
    }
}
