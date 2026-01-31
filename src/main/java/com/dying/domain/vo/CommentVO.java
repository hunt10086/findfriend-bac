package com.dying.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author daylight
 * @Date 2025/7/18 11:44
 */
@Data
public class CommentVO {
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 评论
     */
    private String content;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 昵称
     */
    private String userName;
}
