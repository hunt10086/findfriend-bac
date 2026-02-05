package com.dying.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author daylight
 * @Date 2025/7/13 23:02
 */
@Data
public class UserVO implements Serializable {

    private Long id;
    /**
     * 昵称
     */
    private String userName;

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
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 个人简介
     */
    private String profile;

    /**
     *  经度 纬度
     */
    private Double latitude;

    private Double longitude;


    /**
     *  距离
     */

    private Double distance;
}
