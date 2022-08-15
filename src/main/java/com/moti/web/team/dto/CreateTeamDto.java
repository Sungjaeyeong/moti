package com.moti.web.team.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateTeamDto {

    @NotNull
    private Long userId;

    @NotBlank
    @Length(max = 10)
    private String teamName;

    @Builder
    public CreateTeamDto(Long userId, String teamName) {
        this.userId = userId;
        this.teamName = teamName;
    }
}
