package com.dying.controller;

import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.FriendRequests;
import com.dying.domain.Friends;
import com.dying.domain.User;
import com.dying.domain.UserVo;
import com.dying.exception.BusinessException;
import com.dying.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 好友关系接口
 *
 * @author daylight
 */
@RestController
@RequestMapping("/friends")
@CrossOrigin(origins = {"http://www.seestars.top:9090", "http://localhost:9090"}, allowCredentials = "true")
@Slf4j
public class FriendsController {

    @Resource
    private FriendsService friendsService;

    @Operation(summary = "同意好友申请")
    @PostMapping("/agree")
    public BaseResponse<Boolean> agreeFriendRequest(@RequestBody FriendRequests friendRequests, HttpServletRequest request) {
        if (friendRequests == null || friendRequests.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        boolean result = friendsService.agreeFriendRequest(friendRequests, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "拒绝好友申请")
    @PostMapping("/disagree")
    public BaseResponse<Boolean> disAgreeFriendRequest(@RequestBody FriendRequests friendRequests, HttpServletRequest request) {
        if (friendRequests == null || friendRequests.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        boolean result = friendsService.disAgreeFriendRequest(friendRequests, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "获取好友列表")
    @GetMapping("/list")
    public BaseResponse<List<UserVo>> getFriendList(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        List<UserVo> friendList = friendsService.getFriendList(request);
        return ResultUtils.success(friendList);
    }

    @Operation(summary = "删除好友")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFriend(@RequestBody Long friendUserId, HttpServletRequest request) {
        if (friendUserId == null || friendUserId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友ID不能为空");
        }

        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        boolean result = friendsService.deleteFriend(request, friendUserId);
        return ResultUtils.success(result);
    }

    @Operation(summary = "检查是否为好友")
    @GetMapping("/check")
    public BaseResponse<UserVo> checkFriend(@RequestParam Long friendUserId, HttpServletRequest request) {
        if (friendUserId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友ID不能为空");
        }

        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        UserVo friend = friendsService.getFriends(request, friendUserId);
        return ResultUtils.success(friend);
    }
}