package com.dying.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 好友申请请求封装类
 *
 * @author daylight
 */
@Data
public class FriendRequestsRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 好友用户ID
     */
    private Long friendUserId;

    /**
     * 申请备注信息
     */
    private String message;

}