package com.moti.web.team.dto;

import com.moti.domain.team.TeamStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeTeamStatusDto {

    @NotNull
    private TeamStatus teamStatus;

    @Builder
    public ChangeTeamStatusDto(TeamStatus teamStatus) {
        this.teamStatus = teamStatus;
    }
}
