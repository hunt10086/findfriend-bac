package com.dying.service;

import com.dying.domain.po.FriendRequests;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author daylight
* @description 针对表【friend_requests(好友申请表)】的数据库操作Service
* @createDate 2025-11-18 19:23:38
*/
public interface FriendRequestsService extends IService<FriendRequests> {

    boolean sendFriendRequest(Long friendUserId, HttpServletRequest request, String message);

    List<FriendRequests> getFriendRequest(HttpServletRequest request);
}
