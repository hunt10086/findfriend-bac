package com.dying.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 好友消息表
 * @TableName friend_messages
 */
@TableName(value ="friend_messages")
@Data
public class FriendMessages {
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 消息状态（0: 未读, 1: 已读）
     */
    private Integer status = 0;
}