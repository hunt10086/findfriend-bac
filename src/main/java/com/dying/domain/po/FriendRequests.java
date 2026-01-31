package com.dying.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 好友申请表
 * @TableName friend_requests
 */
@TableName(value ="friend_requests")
@Data
public class FriendRequests {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long fromUserId;

    /**
     * 
     */
    private Long toUserId;

    /**
     * 申请备注
     */
    private String message;

    /**
     * 0-待处理, 1-已同意, 2-已拒绝
     */
    private Integer status;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;
}