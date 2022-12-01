package com.zjj.blog.security;

import com.alibaba.fastjson2.JSON;
import com.zjj.blog.vo.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.zjj.blog.constant.CommonConst.APPLICATION_JSON;
import static com.zjj.blog.enums.HttpStatusEnum.NO_LOGIN;

/**
 * @author 知白守黑
 * @date 2022/10/18 21:19
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setContentType(APPLICATION_JSON);
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(Result.fail(NO_LOGIN.getCode(), NO_LOGIN.getMessage())));
        writer.flush();
        writer.close();
    }
}
