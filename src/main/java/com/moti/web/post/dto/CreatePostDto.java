package com.moti.web.post.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePostDto {

    @NotBlank
    @Length(max = 20)
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Long userId;

    private List<MultipartFile> multipartFiles = new ArrayList<>();

    @Builder
    public CreatePostDto(String title, String content, Long userId, List<MultipartFile> multipartFiles) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        if (multipartFiles != null) {
            this.multipartFiles = multipartFiles;
        }
    }
}
