package com.dying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.po.FriendRequests;
import com.dying.domain.po.Friends;
import com.dying.domain.vo.UserVO;

import java.util.List;

/**
* @author daylight
* @description 针对表【friends(好友关系表)】的数据库操作Service
* @createDate 2025-11-18 19:21:01
*/
public interface FriendsService extends IService<Friends> {
    boolean agreeFriendRequest(FriendRequests friendRequests, UserVO loginUser);

    boolean disAgreeFriendRequest(FriendRequests friendRequests, UserVO loginUser);

    UserVO getFriends(UserVO loginUser, Long friendUserId);

    List<UserVO> getFriendList(UserVO loginUser);

    boolean deleteFriend(UserVO loginUser, Long friendUserId);
}
