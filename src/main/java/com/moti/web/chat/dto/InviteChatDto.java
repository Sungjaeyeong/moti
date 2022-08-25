package com.moti.web.chat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InviteChatDto {
    @NotNull
    private Long chatId;

    @NotNull
    private Long userId;

    @Builder
    public InviteChatDto(Long chatId, Long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }
}
