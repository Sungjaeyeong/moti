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

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Length(min = 5, max = 20)
    private String pw;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Length(max = 10)
    private String name;

    @NotNull(message = "직업은 필수 입력값입니다.")
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
