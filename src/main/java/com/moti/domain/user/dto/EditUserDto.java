package com.moti.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditUserDto {

    @NotBlank
    private String name;

    @NotNull
    private String introduce;

    @Builder
    public EditUserDto(String name, String introduce) {
        this.name = name;
        this.introduce = introduce;
    }
}
