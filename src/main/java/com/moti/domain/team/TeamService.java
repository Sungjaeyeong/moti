package com.moti.domain.team;

import com.moti.domain.chat.ChatRepository;
import com.moti.domain.chat.ChatService;
import com.moti.domain.chat.entity.Chat;
import com.moti.domain.message.MessageService;
import com.moti.domain.team.entity.Team;
import com.moti.domain.team.entity.TeamUser;
import com.moti.domain.user.UserRepository;
import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final ChatService chatService;

    private static final String CREATE_TEAM_SYSTEM_MSG = "팀 채팅방이 생성되었습니다.";
    private static final String JOIN_TEAM_SYSTEM_MSG = " 님이 팀에 참여했습니다. 환영해주세요!";

    // 팀 생성
    @Transactional
    public Team createTeam(Long userId, String teamName) {
        User user = userRepository.findOne(userId);

        Team team = Team.createTeam(teamName, user);

        teamRepository.save(team);
        messageService.createSystemMessage(CREATE_TEAM_SYSTEM_MSG, team.getChat());

        return team;
    }

    // 팀 가입
    @Transactional
    public void joinTeam(Long userId, Long teamId) {
        Team team = teamRepository.findOne(teamId);
        User user = userRepository.findOne(userId);
        TeamUser teamUser = TeamUser.createTeamUser(user);

        team.addTeamUser(teamUser);
        chatService.inviteChat(team.getChat().getId(), userId);
        messageService.createSystemMessage(user.getName() + JOIN_TEAM_SYSTEM_MSG, team.getChat());
    }

    public List<Team> getTeams(int page, int limit, Long userId) {
        if (userId != null) {
            return findTeamsByUser(userId);
        }
        return findTeams(getOffset(page, limit), limit);
    }

    private int getOffset(int page, int limit) {
        return page * limit - limit;
    }

    // 유저가 속한 팀 조회
    public List<Team> findTeamsByUser(Long userId) {
        User user = userRepository.findOne(userId);
        return teamRepository.findTeamsByUser(user);
    }

    // 팀 조회 (유저포함)
    public Team findTeamByTeam(Long teamId) {
        return teamRepository.findTeamByTeam(teamId);
    }

    // 모든 팀 조회
    public List<Team> findTeams(int offset, int limit) {
        return teamRepository.findAll(offset, limit);
    }

    // 팀 탈퇴
    @Transactional
    public void leaveTeam(Long userId, Long teamId) {
        Team team = teamRepository.findOne(teamId);
        User user = userRepository.findOne(userId);

        team.removeTeamUser(user);
        if (team.getTeamUsers().size() == 0) {
            teamRepository.delete(team);
        }

        chatService.exitChat(team.getChat().getId(), userId);
    }

    // 팀 상태 변경
    @Transactional
    public void changeTeamStatus(Long teamId, TeamStatus teamStatus) {
        Team team = teamRepository.findOne(teamId);

        team.changeTeamStatus(teamStatus);
    }

}
