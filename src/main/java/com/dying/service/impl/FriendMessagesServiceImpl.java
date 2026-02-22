package com.dying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.po.FriendMessages;
import com.dying.domain.po.Friends;
import com.dying.exception.BusinessException;
import com.dying.mapper.FriendMessagesMapper;
import com.dying.mapper.FriendsMapper;
import com.dying.service.FriendMessagesService;
import jakarta.annotation.Resource;
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

    @Resource
    private FriendsMapper friendsMapper;

    @Override
    public boolean sendFriendMessage(Long senderId, Long receiverId, String content) {
        try {
            // 验证是否是好友关系
            QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", senderId)
                    .eq("friend_id", receiverId)
                    .eq("status", 1);
            Friends friend = friendsMapper.selectOne(queryWrapper);
            if (friend == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "你们不是好友，无法发送消息");
            }

            FriendMessages message = new FriendMessages();
            message.setSenderId(senderId);
            message.setReceiverId(receiverId);
            message.setMessageContent(content);
            message.setSendTime(new Date());
            // 新消息默认为正常状态（未读），根据表注释：0-已删除，1-正常
            message.setStatus(1);
            return this.save(message);
        } catch (BusinessException e) {
            throw e;
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




