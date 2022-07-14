package com.moti.web.user.dto;

import com.moti.domain.user.entity.Job;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Getter
@ToString
public class AddUserDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 5, max = 20)
    private String pw;

    @NotBlank
    @Length(max = 10)
    private String name;

    @NotNull
    private Job job;

    protected AddUserDto() {
    }

    @Builder
    public AddUserDto(String email, String pw, String name, Job job) {
        this.email = email;
        this.pw = pw;
        this.name = name;
        this.job = job;
    }
}
