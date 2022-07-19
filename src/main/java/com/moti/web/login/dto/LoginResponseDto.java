package com.moti.web.login.dto;

import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponseDto {

    private Long id;
    private String email;
    private String name;
    private Job job;
    private String introduce;

    @Builder
    public LoginResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.job = user.getJob();
        this.introduce = user.getIntroduce();
    }
}

