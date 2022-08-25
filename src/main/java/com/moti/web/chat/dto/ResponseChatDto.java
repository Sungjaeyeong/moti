package com.moti.web.chat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseChatDto {

    private Long chatId;
    private String chatName;
    private int userCount;
    private String createdAt;
    private String updatedAt;

    @Builder
    public ResponseChatDto(Long chatId, String chatName, int userCount, String createdAt, String updatedAt) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.userCount = userCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
