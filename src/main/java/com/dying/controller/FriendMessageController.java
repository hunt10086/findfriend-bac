package com.dying.controller;

import com.dying.common.BaseResponse;
import com.dying.common.ResultUtils;
import com.dying.domain.FriendMessages;
import com.dying.domain.User;
import com.dying.exception.BusinessException;
import com.dying.common.ErrorCode;
import com.dying.service.FriendMessagesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/friendMessage")
public class FriendMessageController {

    @Autowired
    private FriendMessagesService friendMessagesService;

    /**
     * 获取与指定好友的聊天记录
     *
     * @param friendId 好友ID
     * @param request  HTTP请求
     * @return 聊天记录列表
     */
    @GetMapping("/list")
    public BaseResponse<List<FriendMessages>> getFriendMessages(@RequestParam Long friendId, HttpServletRequest request) {
        // 检查用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        User currentUser = (User) userObj;
        Long userId = currentUser.getId();

        // 获取聊天记录
        List<FriendMessages> messages = friendMessagesService.getFriendMessages(userId, friendId);

        // 标记这些消息为已读
        friendMessagesService.markMessagesAsRead(userId, friendId);

        return ResultUtils.success(messages);
    }

    /**
     * 获取未读消息数量
     *
     * @param request HTTP请求
     * @return 未读消息数量
     */
    @GetMapping("/unreadCount")
    public BaseResponse<Integer> getUnreadMessageCount(HttpServletRequest request) {
        // 检查用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        User currentUser = (User) userObj;
        Long userId = currentUser.getId();

        // 获取未读消息数量
        int unreadCount = friendMessagesService.getUnreadMessageCount(userId);
        return ResultUtils.success(unreadCount);
    }

    /**
     * 标记消息为已读
     *
     * @param friendId 好友ID
     * @param request  HTTP请求
     * @return 是否成功
     */
    @PostMapping("/markAsRead")
    public BaseResponse<Boolean> markMessagesAsRead(@RequestParam Long friendId, HttpServletRequest request) {
        // 检查用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        User currentUser = (User) userObj;
        Long userId = currentUser.getId();

        // 标记与指定好友的未读消息为已读
        int updatedCount = friendMessagesService.markMessagesAsRead(userId, friendId);
        return ResultUtils.success(updatedCount >= 0);
    }
}