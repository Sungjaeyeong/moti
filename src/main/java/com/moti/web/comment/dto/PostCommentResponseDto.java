package com.moti.web.comment.dto;

import com.moti.domain.comment.Comment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCommentResponseDto {

    private Long contentId;
    private String content;
    private String userName;
    private Long userId;

    public PostCommentResponseDto(Comment comment) {
        this.contentId = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getUser().getName();
        this.userId = comment.getUser().getId();
    }

}
