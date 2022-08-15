package com.moti.web.team.dto;

import com.moti.domain.team.TeamStatus;
import com.moti.web.user.dto.ResponseUserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseTeamDto {

    private Long teamId;
    private String teamName;
    private TeamStatus teamStatus;
    private List<ResponseUserDto> teamUsers;

    @Builder
    public ResponseTeamDto(Long teamId, String teamName, TeamStatus teamStatus, List<ResponseUserDto> teamUsers) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamStatus = teamStatus;
        this.teamUsers = teamUsers;
    }
}
