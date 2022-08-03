package com.moti.web.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WriteCommentResponseDto {

    private Long id;

    public WriteCommentResponseDto(Long id) {
        this.id = id;
    }
}
