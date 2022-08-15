package com.moti.web.team.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTeamResponseDto {

    private Long id;

    public CreateTeamResponseDto(Long id) {
        this.id = id;
    }
}
