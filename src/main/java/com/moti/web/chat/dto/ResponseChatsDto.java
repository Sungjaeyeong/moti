package com.moti.web.chat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseChatsDto {

    private List<ResponseChatDto> chats;
    private int count;

    @Builder
    public ResponseChatsDto(List<ResponseChatDto> chats, int count) {
        this.chats = chats;
        this.count = count;
    }
}
