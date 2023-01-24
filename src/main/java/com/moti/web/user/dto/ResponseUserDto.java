package com.moti.web.user.dto;

import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ResponseUserDto {

    private final Long id;
    private final String email;
    private final String name;
    private final Job job;
    private final String introduce;

    @Builder
    public ResponseUserDto(Long id, String email, String name, Job job, String introduce) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.job = job;
        this.introduce = introduce;
    }

    public ResponseUserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.job = user.getJob();
        this.introduce = user.getIntroduce();
    }
}
