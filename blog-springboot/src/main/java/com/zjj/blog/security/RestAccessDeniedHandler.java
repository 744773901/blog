package com.zjj.blog.security;

import com.alibaba.fastjson2.JSON;
import com.zjj.blog.vo.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.zjj.blog.constant.CommonConst.APPLICATION_JSON;
import static com.zjj.blog.enums.HttpStatusEnum.NO_AUTHORIZATION;

/**
 * @author 知白守黑
 * @date 2022/10/18 21:38
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        response.setContentType(APPLICATION_JSON);
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(Result.fail(NO_AUTHORIZATION.getCode(), NO_AUTHORIZATION.getMessage())));
        writer.flush();
        writer.close();
    }
}
