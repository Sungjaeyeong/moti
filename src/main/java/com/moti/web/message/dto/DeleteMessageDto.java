package com.moti.web.message.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class DeleteMessageDto {

    @NotNull
    private Long userId;

    @Builder
    public DeleteMessageDto(Long userId) {
        this.userId = userId;
    }
}
