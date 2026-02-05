package com.dying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.po.Team;
import com.dying.domain.vo.UserVO;
import com.dying.domain.request.CreateTeamRequest;
import com.dying.domain.vo.TeamVO;

import java.util.List;


/**
* @author 666
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-07-09 14:30:23
*/
public interface TeamService extends IService<Team> {

    boolean createTeam(CreateTeamRequest createTeamRequest, UserVO loginUser);

    List<TeamVO> getTeamList(UserVO loginUser);

    boolean updateTeam(Long id, UserVO loginUser, TeamVO teamVO);

    boolean joinTeam(TeamVO teamVO, UserVO loginUser, String password);

    boolean quitTeam(TeamVO teamVO, UserVO loginUser);

    boolean deleteTeam(TeamVO teamVO, UserVO loginUser);

    List<TeamVO> getMyTeam(UserVO loginUser);

    List<TeamVO> getJoinTeam(UserVO loginUser);

    List<TeamVO> getTeams(UserVO logonUser);

    List<TeamVO> searchTeam(String teamName, UserVO loginUser);

    List<TeamVO> getOneTeam(Long id, UserVO loginUser);
}
