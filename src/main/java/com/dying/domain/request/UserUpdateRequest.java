package com.dying.domain.request;

import lombok.Data;

/**
 * @author daylight
 * @Date 2026/2/5 17:22
 */
@Data
public class UserUpdateRequest {
    /**
     * 昵称
     */
    private String userName;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别（0-未知 1-男 2-女）
     */
    private Integer gender;
    /**
     * 标签
     */
    private String tags;

    /**
     * 个人简介
     */
    private String profile;

    /**
     *  经度 纬度
     */
    private Double latitude;

    private Double longitude;

}
