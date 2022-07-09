package com.moti.domain.login;

import com.moti.domain.user.UserService;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
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
        user = new User("aaa@gmail.com", "abcdef", "user1", Job.DEV);
        userService.join(user);
    }

    @Test
    @DisplayName("로그인 성공 - 서비스")
    public void succeed_login() throws Exception {
        // when
        User loginUser = loginService.login("aaa@gmail.com", "abcdef");

        // then
        Assertions.assertThat(loginUser).isEqualTo(user);
    }

    @Test
    @DisplayName("로그인 실패 - 서비스")
    public void failed_login() throws Exception {
        // when
        User notEqualPwUser = loginService.login("aaa@gmail.com", "aaaaaaa");
        try {
            loginService.login("bbb@gmail.com", "abcdef");
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        // then
        Assertions.assertThat(notEqualPwUser).isEqualTo(null);
        fail("예외가 발생해야함.");
    }
}