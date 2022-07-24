package com.moti.web.post.dto;

import com.moti.domain.file.File;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    private List<File> files;

    @Builder
    public CreatePostDto(String title, String content, Long userId, List<File> files) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.files = files;

    }
}
