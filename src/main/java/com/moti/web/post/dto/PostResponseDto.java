package com.moti.web.post.dto;

import com.moti.domain.file.File;
import com.moti.domain.post.Post;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String userName;
    private List<FileDto> files;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUser().getId();
        this.userName = post.getUser().getName();
        this.files = post.getFiles().stream().map(file -> new FileDto(file)).collect(Collectors.toList());
    }

    @Data
    static class FileDto {
        private Long id;
        private String uploadFileName;
        private String storeFileName;
        private String location;

        public FileDto(File file) {
            this.id = file.getId();
            this.uploadFileName = file.getUploadFileName();
            this.storeFileName = file.getStoreFileName();
            this.location = file.getLocation();
        }
    }
}
