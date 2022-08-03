package com.moti.web.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WriteCommentDto {

    @NotNull
    private String content;

    @NotNull
    private Long postId;

    @NotNull
    private Long userId;

    @Builder
    public WriteCommentDto(String content, Long postId, Long userId) {
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }
}
