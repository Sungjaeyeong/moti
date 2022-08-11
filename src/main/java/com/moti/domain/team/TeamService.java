package com.moti.domain.team;

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

    // 팀 생성
    @Transactional
    public Team createTeam(Long userId, String teamName) {
        User user = userRepository.findOne(userId);

        TeamUser teamUser = TeamUser.createTeamUser(user);
        Team team = Team.createTeam(teamName, teamUser);

        teamRepository.save(team);
        return team;
    }

    // 팀 가입
    @Transactional
    public void joinTeam(Long userId, Long teamId) {
        Team team = teamRepository.findOne(teamId);
        User user = userRepository.findOne(userId);

        TeamUser teamUser = TeamUser.createTeamUser(user);

        team.addTeamUser(teamUser);
    }

    // 팀 조회 (유저포함)
    public Team findTeamByTeam(Long teamId) {
        return teamRepository.findTeamByTeam(teamId);
    }

    // 특정 유저의 팀 조회 (유저포함)
    public Team findTeamByUser(Long userId) {
        return teamRepository.findTeamByUser(userId);
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
    }

    // 팀 상태 변경
    @Transactional
    public void changeTeamStatus(Long teamId, TeamStatus teamStatus) {
        Team team = teamRepository.findOne(teamId);

        team.changeTeamStatus(teamStatus);
    }

}
