package com.dying.websocket;

import com.dying.domain.po.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

@Component
public class FriendHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 握手前拦截
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            HttpSession session = httpRequest.getSession(false);

            if (session != null) {
                // 将HTTP session放入attributes中供后续使用
                attributes.put("HTTP_SESSION", session);

                // 验证用户是否已登录
                Object userObj = session.getAttribute(USER_LOGIN_STATE);
                if (userObj instanceof User) {
                    // 允许建立连接
                    return true;
                }
            }
        }

        // 用户未登录，拒绝连接
        return false;
    }

    /**
     * 握手后处理
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后执行的操作（如果需要）
    }
}