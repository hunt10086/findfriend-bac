package com.dying.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 好友关系表
 * @TableName friends
 */
@TableName(value ="friends")
@Data
public class Friends {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 好友ID
     */
    private Long friendId;

    /**
     * 好友状态：0-已删除, 1-正常好友
     */
    private Integer status;

    /**
     * 成为好友时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}