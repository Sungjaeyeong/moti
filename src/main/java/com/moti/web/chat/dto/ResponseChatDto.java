package com.moti.web.chat.dto;

import com.moti.domain.chat.entity.Chat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

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

    public ResponseChatDto(Chat chat) {
        this.chatId = chat.getId();
        this.chatName = chat.getName();
        this.userCount = chat.getChatUsers().size();
        this.createdAt = chat.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.updatedAt = chat.getLastModifiedDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
