package com.moti.web.post.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditPostControllerDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private List<MultipartFile> multipartFiles = new ArrayList<>();

    @Builder
    public EditPostControllerDto(String title, String content, List<MultipartFile> multipartFiles) {
        this.title = title;
        this.content = content;
        if (multipartFiles != null) {
            this.multipartFiles = multipartFiles;
        }
    }
}
