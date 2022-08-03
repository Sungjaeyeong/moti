package com.moti.web.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditCommentDto {

    @NotNull
    private String comment;

    @NotNull
    private Long userId;
}
