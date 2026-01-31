package com.dying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.domain.po.FriendMessages;
import com.dying.mapper.FriendMessagesMapper;
import com.dying.service.FriendMessagesService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author daylight
 * @description 针对表【friend_messages(好友消息表)】的数据库操作Service实现
 * @createDate 2025-11-18 19:23:41
 */
@Service
public class FriendMessagesServiceImpl extends ServiceImpl<FriendMessagesMapper, FriendMessages>
        implements FriendMessagesService {

    @Override
    public boolean sendFriendMessage(Long senderId, Long receiverId, String content) {
        try {
            FriendMessages message = new FriendMessages();
            message.setSenderId(senderId);
            message.setReceiverId(receiverId);
            message.setMessageContent(content);
            message.setSendTime(new Date());
            // 新消息默认为未读状态
            message.setStatus(0);
            return this.save(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<FriendMessages> getFriendMessages(Long userId, Long friendId) {
        QueryWrapper<FriendMessages> queryWrapper = new QueryWrapper<>();
        // 查询双方的聊天记录
        queryWrapper.and(wrapper -> wrapper.eq("sender_id", userId).eq("receiver_id", friendId)
                        .or()
                        .eq("sender_id", friendId).eq("receiver_id", userId))
                .orderByAsc("send_time");
        return this.list(queryWrapper);
    }

    @Override
    public int getUnreadMessageCount(Long userId) {
        // 查询用户未读消息数量
        QueryWrapper<FriendMessages> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId).eq("status", 0);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public int markMessagesAsRead(Long userId, Long friendId) {
        // 更新与指定好友的未读消息为已读状态
        UpdateWrapper<FriendMessages> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("receiver_id", userId)
                .eq("sender_id", friendId)
                .eq("status", 0)
                .set("status", 1);
        boolean result = this.update(updateWrapper);
        // 如果更新成功，返回1，否则返回0
        return result ? 1 : 0;
    }
}




