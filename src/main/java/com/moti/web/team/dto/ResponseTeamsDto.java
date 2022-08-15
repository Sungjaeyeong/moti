package com.moti.web.team.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseTeamsDto {

    private List<ResponseTeamDto> responseTeamDtoList;
    private int count;

    @Builder
    public ResponseTeamsDto(List<ResponseTeamDto> responseTeamDtoList, int count) {
        this.responseTeamDtoList = responseTeamDtoList;
        this.count = count;
    }
}
