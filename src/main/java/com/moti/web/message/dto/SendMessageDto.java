package com.moti.web.message.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class SendMessageDto {

    @NotBlank
    private String message;

    @NotNull
    private Long userId;

    @NotNull
    private Long chatId;

    @Builder
    public SendMessageDto(String message, Long userId, Long chatId) {
        this.message = message;
        this.userId = userId;
        this.chatId = chatId;
    }
}
