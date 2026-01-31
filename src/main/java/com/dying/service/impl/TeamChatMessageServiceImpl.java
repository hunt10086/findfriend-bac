package com.dying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dying.domain.po.TeamChatMessage;
import com.dying.domain.po.User;
import com.dying.domain.po.UserTeam;
import com.dying.domain.vo.TeamChatMessageVO;
import com.dying.mapper.TeamChatMessageMapper;
import com.dying.mapper.UserMapper;
import com.dying.mapper.UserTeamMapper;
import com.dying.service.TeamChatMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TeamChatMessageServiceImpl implements TeamChatMessageService {
    @Autowired
    private TeamChatMessageMapper teamChatMessageMapper;
    @Autowired
    private UserTeamMapper userTeamMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean sendTeamChatMessage(Long userId, Long teamId, String content) {
        // 校验是否为队伍成员
        UserTeam userTeam = userTeamMapper.selectOne(new QueryWrapper<UserTeam>()
                .eq("user_id", userId)
                .eq("team_id", teamId));
        if (userTeam == null) {
            return false;
        }
        // 不存储__check_only__消息
        if ("__check_only__".equals(content)) {
            return true;
        }
        TeamChatMessage message = new TeamChatMessage();
        message.setTeamId(teamId);
        message.setUserId(userId);
        message.setContent(content);
        message.setCreateTime(new Date());
        return teamChatMessageMapper.insert(message) > 0;
    }

    @Override
    public List<TeamChatMessageVO> getTeamChatMessages(Long teamId) {
        List<TeamChatMessage> messageList = teamChatMessageMapper.selectList(
                new QueryWrapper<TeamChatMessage>().eq("team_id", teamId).orderByAsc("create_time")
        );
        List<TeamChatMessageVO> voList = new ArrayList<>();
        for (TeamChatMessage msg : messageList) {
            TeamChatMessageVO vo = new TeamChatMessageVO();
            BeanUtils.copyProperties(msg, vo);
            User user = userMapper.selectById(msg.getUserId());
            vo.setUserName(user != null ? user.getUserName() : "");
            vo.setAvatarUrl(user != null ? user.getAvatarUrl() : null);
            voList.add(vo);
        }
        return voList;
    }
} 