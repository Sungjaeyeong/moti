package com.moti.domain.post.dto;

import com.moti.domain.file.File;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditPostDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private List<File> fileList;

    @Builder
    public EditPostDto(String title, String content, List<File> fileList) {
        this.title = title;
        this.content = content;
        this.fileList = fileList;
    }
}
