package com.moti.web.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePostResponseDto {

    private Long id;

    public CreatePostResponseDto(Long id) {
        this.id = id;
    }
}
