package com.dying.service;

import com.dying.domain.vo.TeamChatMessageVO;

import java.util.List;

public interface TeamChatMessageService {
    boolean sendTeamChatMessage(Long userId, Long teamId, String content);
    List<TeamChatMessageVO> getTeamChatMessages(Long teamId);
} 