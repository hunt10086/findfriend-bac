package com.dying.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.po.FriendRequests;
import com.dying.domain.po.Friends;
import com.dying.domain.po.User;
import com.dying.exception.BusinessException;
import com.dying.mapper.FriendsMapper;
import com.dying.service.FriendRequestsService;
import com.dying.mapper.FriendRequestsMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author daylight
 * @description 针对表【friend_requests(好友申请表)】的数据库操作Service实现
 * @createDate 2025-11-18 19:23:38
 */
@Service
public class FriendRequestsServiceImpl extends ServiceImpl<FriendRequestsMapper, FriendRequests>
        implements FriendRequestsService {

    @Resource
    private FriendsMapper friendsMapper;

    @Override
    public boolean sendFriendRequest(Long friendUserId, HttpServletRequest request, String message) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Long userId = user.getId();
        if (userId == null || friendUserId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }
        if (userId.equals(friendUserId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能添加自己为好友");
        }
        //检查好友关系是否已存在
        Friends existingFriend = friendsMapper.selectById(friendUserId);
        if (existingFriend != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友关系已存在");
        }
        //创建好友关系
        FriendRequests friendRequests = new FriendRequests();
        friendRequests.setStatus(0);
        friendRequests.setFromUserId(userId);
        friendRequests.setToUserId(friendUserId);
        friendRequests.setMessage(message);
        return this.save(friendRequests);
    }

    @Override
    public List<FriendRequests> getFriendRequest(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Long userId = user.getId();
        return this.lambdaQuery().eq(FriendRequests::getToUserId, userId).list();
    }

}




