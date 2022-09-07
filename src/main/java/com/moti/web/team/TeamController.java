package com.moti.web.team;

import com.moti.domain.team.TeamService;
import com.moti.domain.team.entity.Team;
import com.moti.domain.team.entity.TeamUser;
import com.moti.web.team.dto.*;
import com.moti.web.user.dto.ResponseUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    // 팀 생성
    @PostMapping
    public CreateTeamResponseDto createTeam(@RequestBody @Validated CreateTeamDto createTeamDto) {
        Team team = teamService.createTeam(createTeamDto.getUserId(), createTeamDto.getTeamName());

        return new CreateTeamResponseDto(team.getId());
    }

    // 팀 가입
    @PostMapping("/{teamId}")
    public void joinTeam(@PathVariable Long teamId, @RequestBody @Validated JoinTeamDto joinTeamDto) {
        teamService.joinTeam(joinTeamDto.getUserId(), teamId);
    }

    // 팀 전부 조회 (유저포함)
    @GetMapping()
    public ResponseTeamsDto findAll(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "5") int limit,
                                            @RequestParam(required = false) Long userId) {
        List<Team> teams;
        teams = teamService.findTeams(getOffset(page, limit), limit);
        if (userId != null) {
            teams = teamService.findTeamsByUser(userId);
        }

        List<ResponseTeamDto> responseTeamDtoList = teams.stream()
                .map(team -> mapResponseTeamDto(team))
                .collect(Collectors.toList());

        return ResponseTeamsDto.builder()
                .responseTeamDtoList(responseTeamDtoList)
                .count(responseTeamDtoList.size())
                .build();
    }

    private int getOffset(int page, int limit) {
        return page * limit - limit;
    }

    // 팀 조회 (팀으로)
    @GetMapping("/{teamId}")
    public ResponseTeamDto findByTeam(@PathVariable Long teamId) {
        Team teamWithUsers = teamService.findTeamByTeam(teamId);
        return mapResponseTeamDto(teamWithUsers);
    }

    private ResponseTeamDto mapResponseTeamDto(Team team) {
        return ResponseTeamDto.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .teamStatus(team.getStatus())
                .teamUsers(team.getTeamUsers().stream()
                        .map(teamUser -> mapResponseUserDto(teamUser))
                        .collect(Collectors.toList())
                )
                .build();
    }

    private ResponseUserDto mapResponseUserDto(TeamUser teamUser) {
        return ResponseUserDto.builder()
                .id(teamUser.getUser().getId())
                .name(teamUser.getUser().getName())
                .email(teamUser.getUser().getEmail())
                .job(teamUser.getUser().getJob())
                .introduce(teamUser.getUser().getIntroduce())
                .build();
    }

    // 팀 상태 변경
    @PatchMapping("/{teamId}")
    public void changeTeamStatus(@PathVariable Long teamId, @RequestBody @Validated ChangeTeamStatusDto changeTeamStatusDto) {
        teamService.changeTeamStatus(teamId, changeTeamStatusDto.getTeamStatus());
    }

    // 팀 탈퇴
    @DeleteMapping("/{teamId}/users/{userId}")
    public void leaveTeam(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.leaveTeam(userId, teamId);
    }

}
