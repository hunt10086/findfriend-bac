package com.dying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.po.FriendRequests;
import com.dying.domain.po.Friends;
import com.dying.domain.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author daylight
* @description 针对表【friends(好友关系表)】的数据库操作Service
* @createDate 2025-11-18 19:21:01
*/
public interface FriendsService extends IService<Friends> {
    boolean agreeFriendRequest(FriendRequests friendRequests, HttpServletRequest request);

    boolean disAgreeFriendRequest(FriendRequests friendRequests, HttpServletRequest request);

    UserVO getFriends(HttpServletRequest request, Long friendUserId);

    List<UserVO> getFriendList(HttpServletRequest request);

    boolean deleteFriend(HttpServletRequest request, Long friendUserId);
}
