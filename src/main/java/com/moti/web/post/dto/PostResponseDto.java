package com.moti.web.post.dto;

import com.moti.domain.file.File;
import com.moti.domain.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private List<File> files;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUser().getId();
        this.files = post.getFiles();
    }

    @Builder
    public PostResponseDto(Long id, String title, String content, Long userId, List<File> files) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.files = files;
    }
}
