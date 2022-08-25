package com.moti.web.chat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateChatDto {

    @NotNull
    private List<Long> userIds;

    @Builder
    public CreateChatDto(List<Long> userIds) {
        this.userIds = userIds;
    }
}
