package com.dying.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.po.FriendRequests;
import com.dying.domain.po.Friends;
import com.dying.domain.po.User;
import com.dying.domain.vo.UserVO;
import com.dying.exception.BusinessException;
import com.dying.mapper.UserMapper;
import com.dying.service.FriendRequestsService;
import com.dying.service.FriendsService;
import com.dying.service.UserService;
import com.dying.mapper.FriendsMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author daylight
 * @description 针对表【friends(好友关系表)】的数据库操作Service实现
 * @createDate 2025-11-18 19:21:01
 */
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends>
        implements FriendsService {

    @Resource
    private FriendRequestsService friendRequestsService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Override
    public boolean agreeFriendRequest(FriendRequests friendRequests, UserVO loginUser) {
        Long requestId = friendRequests.getId();
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Long userId = loginUser.getId();
        FriendRequests friendRequest = friendRequestsService.getById(requestId);
        if (friendRequest == null || !friendRequest.getToUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友请求不存在或无权限操作");
        }
        //创建好友关系
        Friends friend = new Friends();
        friend.setUserId(friendRequest.getFromUserId());
        friend.setFriendId(friendRequest.getToUserId());
        this.save(friend);
        Friends reverseFriend = new Friends();
        reverseFriend.setUserId(friendRequest.getToUserId());
        reverseFriend.setFriendId(friendRequest.getFromUserId());
        this.save(reverseFriend);
        //更新好友请求状态
        friendRequest.setStatus(1); //已同意
        return friendRequestsService.updateById(friendRequest);
    }

    @Override
    public boolean disAgreeFriendRequest(FriendRequests friendRequests, UserVO loginUser) {
        Long requestId = friendRequests.getId();
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Long userId = loginUser.getId();
        FriendRequests friendRequest = friendRequestsService.getById(requestId);
        if (friendRequest == null || !friendRequest.getToUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友请求不存在或无权限操作");
        }
        friendRequest.setStatus(2); //拒绝
        return friendRequestsService.updateById(friendRequest);
    }

    @Override
    public UserVO getFriends(UserVO loginUser, Long friendUserId) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Long userId = loginUser.getId();
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("friend_id", friendUserId)
                .eq("status", 1);
        Friends friendRelation = this.getOne(queryWrapper);
        // 检查好友关系是否存在
        if (friendRelation == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "你们不是好友");
        }
        User friendUser = userMapper.selectById(friendUserId);
        if (friendUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友不存在");
        } else {
            return userService.getSafetyUser(friendUser);
        }
    }

    @Override
    public List<UserVO> getFriendList(UserVO loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Long userId = loginUser.getId();
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("status", 1);
        return this.list(queryWrapper).stream()
                .map(friends1 -> {
                    User friendUser = userMapper.selectById(friends1.getFriendId());
                    if (friendUser == null) {
                        return null;
                    }
                    return userService.getSafetyUser(friendUser);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public boolean deleteFriend(UserVO loginUser, Long friendUserId) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Long userId = loginUser.getId();
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("friend_id", friendUserId)
                .eq("status", 1);
        Friends friend = this.getOne(queryWrapper);
        if (friend == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友关系不存在");
        }
        friend.setStatus(0); //删除好友关系

        QueryWrapper<Friends> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("user_id", friendUserId)
                .eq("friend_id", userId)
                .eq("status", 1);
        Friends friend2 = this.getOne(queryWrapper2);
        boolean flag = false;
        if (friend2 != null) {
            friend2.setStatus(0);
            flag = this.updateById(friend2);
        }
        return this.updateById(friend) && flag;
    }


}




