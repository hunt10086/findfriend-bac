package com.dying.websocket;

import com.dying.domain.po.FriendMessages;
import com.dying.domain.vo.UserVO;
import com.dying.service.FriendMessagesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author daylight
 */
@Component
public class FriendMessageWebSocketHandler extends TextWebSocketHandler {

    // 存储在线用户连接
    private static final Map<Long, WebSocketSession> ONLINE_USERS = new ConcurrentHashMap<>();

    private final FriendMessagesService friendMessagesService;
    private final ObjectMapper objectMapper;

    public FriendMessageWebSocketHandler(FriendMessagesService friendMessagesService) {
        this.friendMessagesService = friendMessagesService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 建立连接
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从session中获取用户ID（在握手拦截器中设置）
        HttpSession httpSession = (HttpSession) session.getAttributes().get("HTTP_SESSION");
        if (httpSession != null) {
            Object userObj = httpSession.getAttribute(USER_LOGIN_STATE);
            if (userObj != null) {
                Long userId = ((UserVO) userObj).getId();
                ONLINE_USERS.put(userId, session);
                System.out.println("用户 " + userId + " 已连接");

                // 发送连接成功消息
                session.sendMessage(new TextMessage("{\"type\":\"connected\",\"message\":\"连接成功\"}"));
            } else {
                // 未登录用户，关闭连接
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("用户未登录"));
            }
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("无法获取HTTP会话"));
        }
    }

    /**
     * 处理收到的消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // 解析消息
            MessageDTO messageDTO = objectMapper.readValue(message.getPayload(), MessageDTO.class);

            // 获取发送者ID
            HttpSession httpSession = (HttpSession) session.getAttributes().get("HTTP_SESSION");
            if (httpSession == null) {
                session.sendMessage(new TextMessage("{\"error\":\"无法获取用户会话\"}"));
                return;
            }

            Object userObj = httpSession.getAttribute(USER_LOGIN_STATE);
            if (userObj == null) {
                session.sendMessage(new TextMessage("{\"error\":\"用户未登录\"}"));
                return;
            }

            Long senderId = ((UserVO) userObj).getId();
            Long receiverId = messageDTO.getReceiverId();
            String content = messageDTO.getContent();

            // 使用服务方法发送消息
            boolean success = friendMessagesService.sendFriendMessage(senderId, receiverId, content);
            if (!success) {
                session.sendMessage(new TextMessage("{\"error\":\"消息保存失败\"}"));
                return;
            }

            // 获取刚刚保存的消息
            FriendMessages friendMessage = new FriendMessages();
            friendMessage.setSenderId(senderId);
            friendMessage.setReceiverId(receiverId);
            friendMessage.setMessageContent(content);
            friendMessage.setSendTime(new Date());
            // 新消息默认为未读状态
            friendMessage.setStatus(0);
            // 构造返回给发送方的消息
            String responseMessage = objectMapper.writeValueAsString(friendMessage);

            // 尝试发送给接收者
            WebSocketSession receiverSession = ONLINE_USERS.get(receiverId);
            if (receiverSession != null && receiverSession.isOpen()) {
                // 接收者在线，直接发送消息
                receiverSession.sendMessage(new TextMessage(responseMessage));
            } else {
                // 如果接收者不在线，消息已存储在数据库中，等待其上线后拉取
                System.out.println("用户 " + receiverId + " 不在线，消息已存储到数据库");
            }

            // 向发送方确认消息已发送
            session.sendMessage(new TextMessage("{\"type\":\"sent\",\"message\":\"消息已发送\",\"data\":" + responseMessage + "}"));

        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"error\":\"消息处理失败: " + e.getMessage() + "\"}"));
        }
    }

    /**
     * 连接关闭
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 移除离线用户
        ONLINE_USERS.values().remove(session);
        System.out.println("WebSocket连接已关闭: " + status);
    }

    /**
     * 发生错误
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket传输错误: " + exception.getMessage());
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
        ONLINE_USERS.values().remove(session);
    }

    /**
     * 向指定用户发送消息
     */
    public static void sendMessageToUser(Long userId, String message) throws IOException {
        WebSocketSession session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(Long userId) {
        WebSocketSession session = ONLINE_USERS.get(userId);
        return session != null && session.isOpen();
    }
}