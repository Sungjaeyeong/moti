package com.moti.domain.login;

import com.moti.domain.user.UserService;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.exception.NotMatchUserException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LoginServiceTest {

    @Autowired LoginService loginService;
    @Autowired UserService userService;
    User user;

    @BeforeEach
    void join() {
        user = User.builder()
                .email("aaa@gmail.com")
                .password("abcdef")
                .name("user1")
                .job(Job.DEV)
                .build();
        userService.join(user);
    }

    @Test
    @DisplayName("로그인 성공 - 서비스")
    public void succeed_login() throws Exception {
        // when
        User loginUser = loginService.login("aaa@gmail.com", "abcdef").get();

        // then
        Assertions.assertThat(loginUser).isEqualTo(user);
    }

    @Test
    @DisplayName("로그인 실패(이메일 틀림) - 서비스")
    public void failed_login1() throws Exception {
        //when
        User notEqualPwUser = loginService.login("bbb@gmail.com", "aaaaaaa").orElse(null);

        // then
        Assertions.assertThat(notEqualPwUser).isEqualTo(null);
    }

    @Test
    @DisplayName("로그인 실패(비밀번호 틀림) - 서비스")
    public void failed_login2() throws Exception {
        // when
        User notEqualPwUser = loginService.login("aaa@gmail.com", "aaaaaaa").orElse(null);

        // then
        Assertions.assertThat(notEqualPwUser).isEqualTo(null);
    }
}
