package com.zjj.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Collections;
import java.util.Optional;

/**
 * @author 知白守黑
 * @date 2022/10/5 17:20
 */
@Configuration
public class WebSocketConfig extends ServerEndpointConfig.Configurator {
    public static final String HEADER_NAME = "X-Real-IP";

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        String realIp = Optional
                .ofNullable(request.getHeaders().get(HEADER_NAME.toLowerCase()))
                .orElseGet(() -> Collections.singletonList("未知IP"))
                .get(0);
        sec.getUserProperties().put(HEADER_NAME, realIp);
    }
}
