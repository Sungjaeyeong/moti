package com.moti.web.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddUserResponseDto {
    private Long id;

    public AddUserResponseDto(Long id) {
        this.id = id;
    }
}
