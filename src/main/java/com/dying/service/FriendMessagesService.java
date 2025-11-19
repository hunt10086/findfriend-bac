package com.dying.service;

import com.dying.domain.FriendMessages;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author daylight
 * @description 针对表【friend_messages(好友消息表)】的数据库操作Service
 * @createDate 2025-11-18 19:23:41
 */
public interface FriendMessagesService extends IService<FriendMessages> {

    /**
     * 发送好友消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @return 是否发送成功
     */
    boolean sendFriendMessage(Long senderId, Long receiverId, String content);

    /**
     * 获取好友消息列表
     * @param userId 当前用户ID
     * @param friendId 好友ID
     * @return 消息列表
     */
    List<FriendMessages> getFriendMessages(Long userId, Long friendId);

    /**
     * 获取未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadMessageCount(Long userId);

    /**
     * 标记与指定好友的未读消息为已读
     * @param userId 当前用户ID
     * @param friendId 好友ID
     * @return 更新的记录数
     */
    int markMessagesAsRead(Long userId, Long friendId);
}
