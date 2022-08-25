package com.moti.web.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChatResponseDto {

    private Long id;

    public CreateChatResponseDto(Long id) {
        this.id = id;
    }
}
