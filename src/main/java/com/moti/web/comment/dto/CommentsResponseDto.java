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

    private Long count;

    @Builder
    public CommentsResponseDto(List<PostCommentResponseDto> comments, Long count) {
        this.comments = comments;
        this.count = count;
    }
}
