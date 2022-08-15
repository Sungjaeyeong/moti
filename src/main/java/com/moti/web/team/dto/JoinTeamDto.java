package com.moti.web.team.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinTeamDto {

    @NotNull
    private Long userId;

    @Builder
    public JoinTeamDto(Long userId) {
        this.userId = userId;
    }
}
