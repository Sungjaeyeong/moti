package com.moti.web.user.dto;

import com.moti.domain.user.entity.Job;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseUserDto {

    private Long id;
    private String email;
    private String name;
    private Job job;
    private String introduce;

    @Builder
    public ResponseUserDto(Long id, String email, String name, Job job, String introduce) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.job = job;
        this.introduce = introduce;
    }
}
