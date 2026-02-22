package com.dying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.po.FriendRequests;
import com.dying.domain.po.Friends;
import com.dying.domain.vo.UserVO;
import com.dying.exception.BusinessException;
import com.dying.mapper.FriendsMapper;
import com.dying.service.FriendRequestsService;
import com.dying.mapper.FriendRequestsMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Resource
    private FriendRequestsMapper friendRequestsMapper;

    @Override
    public boolean sendFriendRequest(Long friendUserId, UserVO loginUser, String message) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }

        if (friendUserId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友 ID 不能为空");
        }

        Long userId = loginUser.getId();

        if (userId.equals(friendUserId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能添加自己为好友");
        }

        long existingFriendCount = friendsMapper.selectCount(
                new LambdaQueryWrapper<Friends>()
                        .eq(Friends::getUserId, userId)
                        .eq(Friends::getFriendId, friendUserId)
        );

        if (existingFriendCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "你们已经是好友了");
        }

        long pendingRequestCount = friendRequestsMapper.selectCount(
                new LambdaQueryWrapper<FriendRequests>()
                        .eq(FriendRequests::getFromUserId, userId)
                        .eq(FriendRequests::getToUserId, friendUserId)
                        .eq(FriendRequests::getStatus, 0)
        );

        if (pendingRequestCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "申请已发送，请等待对方处理");
        }

        FriendRequests friendRequest = new FriendRequests();
        friendRequest.setFromUserId(userId);
        friendRequest.setToUserId(friendUserId);
        friendRequest.setMessage(message);
        friendRequest.setStatus(0);

        return this.save(friendRequest);
    }


    @Override
    public List<FriendRequests> getFriendRequest(UserVO loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Long userId = loginUser.getId();
        return this.lambdaQuery().eq(FriendRequests::getToUserId, userId).list();
    }

}




