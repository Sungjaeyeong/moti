package com.moti.web.user.dto;

import com.moti.domain.user.entity.Job;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddUserDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 5, max = 20)
    private String password;

    @NotBlank
    @Length(max = 10)
    private String name;

    @NotNull
    private Job job;

    private String introduce;

    @Builder
    public AddUserDto(String email, String password, String name, Job job, String introduce) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.job = job;
        this.introduce = introduce;
    }
}
