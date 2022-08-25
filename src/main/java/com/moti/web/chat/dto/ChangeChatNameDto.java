package com.moti.web.chat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeChatNameDto {

    @NotNull
    private Long chatId;

    @NotBlank
    @Length(max = 10)
    private String name;

    @Builder
    public ChangeChatNameDto(Long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }
}
