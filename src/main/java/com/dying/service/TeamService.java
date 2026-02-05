package com.dying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.po.Team;
import com.dying.domain.po.User;
import com.dying.domain.request.CreateTeamRequest;
import com.dying.domain.vo.TeamVO;

import java.util.List;


/**
* @author 666
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-07-09 14:30:23
*/
public interface TeamService extends IService<Team> {

    boolean createTeam(CreateTeamRequest createTeamRequest, User loginUser);

    List<TeamVO> getTeamList(User loginUser);

    boolean updateTeam(Long id, User loginUser, TeamVO teamVO);

    boolean joinTeam(TeamVO teamVO, User loginUser, String password);

    boolean quitTeam(TeamVO teamVO, User loginUser);

    boolean deleteTeam(TeamVO teamVO, User loginUser);

    List<TeamVO> getMyTeam(User loginUser);

    List<TeamVO> getJoinTeam(User loginUser);

    List<TeamVO> getTeams(User logonUser);

    List<TeamVO> searchTeam(String teamName, User loginUser);

    List<TeamVO> getOneTeam(Long id, User loginUser);
}
