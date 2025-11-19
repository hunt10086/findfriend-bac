package com.dying.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import jakarta.annotation.Resource;

@Configuration
@EnableWebSocket
public class FriendWebSocketConfig implements WebSocketConfigurer {

    @Resource
    private FriendMessageWebSocketHandler friendMessageWebSocketHandler;

    @Resource
    private FriendHandshakeInterceptor friendHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(friendMessageWebSocketHandler, "/ws/friend")
                .addInterceptors(friendHandshakeInterceptor)
                .setAllowedOrigins("*"); // 根据实际情况设置允许的来源
    }
}