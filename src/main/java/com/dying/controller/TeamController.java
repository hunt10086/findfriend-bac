package com.dying.controller;

import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.po.Team;
import com.dying.domain.vo.UserVO;
import com.dying.domain.request.CreateTeamRequest;
import com.dying.domain.vo.TeamVO;
import com.dying.exception.BusinessException;
import com.dying.mapper.TeamMapper;
import com.dying.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;


/**
 * @Author daylight
 * @Date 2025/7/9 15:02
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private TeamMapper teamMapper;

    @Operation(summary = "创建队伍")
    @PostMapping("/create")
    public BaseResponse<Boolean> addTeam(@RequestBody CreateTeamRequest createTeamRequest, HttpServletRequest request) {
        if(createTeamRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍参数为空");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        boolean flag=teamService.createTeam(createTeamRequest, user);
        return ResultUtils.success(flag);
    }

    @Operation(summary = "获取队伍列表")
    @GetMapping("/list")
    public BaseResponse<List<TeamVO>> listTeam(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        List<TeamVO> res=teamService.getTeamList(user);
        return ResultUtils.success(res,res.size());
    }

    @Operation(summary = "更新队伍")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamVO teamVO, HttpServletRequest request, Long id) {
        if(teamVO ==null||id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"更新队伍参数为空");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        boolean flag=teamService.updateTeam(id,user, teamVO);
        return ResultUtils.success(flag);
    }

    @Operation(summary = "加入队伍")
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamVO teamVO, HttpServletRequest request, String password) {
        if(teamVO ==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"加入队伍不存在");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        return ResultUtils.success(teamService.joinTeam(teamVO,user,password));
    }

    @Operation(summary = "退出队伍")
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamVO teamVO, HttpServletRequest request) {
        if(teamVO ==null|| teamVO.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        return ResultUtils.success(teamService.quitTeam(teamVO,user));
    }

    @Operation(summary = "删除队伍")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamVO teamVO, HttpServletRequest request) {
        if(teamVO ==null|| teamVO.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        return ResultUtils.success(teamService.deleteTeam(teamVO,user));
    }

    @Operation(summary = "展示创建的队伍列表")
    @GetMapping("/myTeam")
    public BaseResponse<List<TeamVO>> getMyTeam(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        List<TeamVO> res=teamService.getMyTeam(user);
        return ResultUtils.success(res);
    }

    @Operation(summary = "展示加入队伍列表")
    @GetMapping("/joinTeam")
    public BaseResponse<List<TeamVO>> getJoinTeam(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        List<TeamVO> res=teamService.getJoinTeam(user);
        return ResultUtils.success(res);
    }

    @Operation(summary = "根据队伍名搜索队伍")
    @GetMapping("/search")
    public BaseResponse<List<TeamVO>> searchTeam(String teamName, HttpServletRequest request) {
        if(teamName==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        List<TeamVO> res=teamService.searchTeam(teamName,user);
        return ResultUtils.success(res);
    }

    @Operation(summary = "根据id查找队伍")
    @GetMapping("searchByID")
    public BaseResponse<Long> searchTeamByID(Long teamId,HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        Team team = teamMapper.selectById(teamId);
        return ResultUtils.success(team.getUserId());
    }

    @Operation(summary = "获取队伍信息")
    @GetMapping("/get/team")
    public BaseResponse<List<TeamVO>> getTeam(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        List<TeamVO> teams=teamService.getTeams(user);
        if(teams==null||teams.size()<=0){
            return ResultUtils.error(null);
        }else{
            return ResultUtils.success(teams);
        }
    }

}
