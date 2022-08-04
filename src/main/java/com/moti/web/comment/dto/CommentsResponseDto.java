package com.moti.web.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentsResponseDto {

    private List<PostCommentResponseDto> comments;

    private int count;

    @Builder
    public CommentsResponseDto(List<PostCommentResponseDto> comments, int count) {
        this.comments = comments;
        this.count = count;
    }
}
