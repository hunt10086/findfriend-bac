package com.dying.controller;

import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.po.FriendRequests;
import com.dying.domain.vo.UserVO;
import com.dying.domain.request.FriendRequestsRequest;
import com.dying.exception.BusinessException;
import com.dying.service.FriendRequestsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 好友申请接口
 *
 * @author daylight
 */
@RestController
@RequestMapping("/friendRequests")
@Slf4j
public class FriendRequestsController {

    @Resource
    private FriendRequestsService friendRequestsService;

    @Operation(summary = "发送好友申请")
    @PostMapping("/send")
    public BaseResponse<Boolean> sendFriendRequest(@RequestBody FriendRequestsRequest friendRequestsRequest, HttpServletRequest request) {
        if (friendRequestsRequest == null || friendRequestsRequest.getFriendUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        UserVO user = (UserVO) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        boolean result = friendRequestsService.sendFriendRequest(
                friendRequestsRequest.getFriendUserId(),
                user,
                friendRequestsRequest.getMessage()
        );
        return ResultUtils.success(result);
    }

    @Operation(summary = "获取好友申请列表")
    @GetMapping("/list")
    public BaseResponse<List<FriendRequests>> getFriendRequests(HttpServletRequest request) {
        UserVO user = (UserVO) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        List<FriendRequests> friendRequests = friendRequestsService.getFriendRequest(user);
        return ResultUtils.success(friendRequests);
    }
}