package com.moti.domain.user;

import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.user.dto.AddUserDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserRepository userRepository;
    @Autowired UserService userService;

    @Test
    @DisplayName("회원가입 - 서비스")
    public void join() throws Exception {
        // given
        AddUserDto addUserDto = AddUserDto.builder()
                .email("jaeyeong@moti.com")
                .pw("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        User user = new User(addUserDto.getEmail(), addUserDto.getPw(), addUserDto.getName(), addUserDto.getJob());

        // when
        Long joinUserId = userService.join(user);

        // then
        Assertions.assertThat(user).isEqualTo(userRepository.findOne(joinUserId));
    }

    @Test
    @DisplayName("중복 회원 예외")
    public void validate_user() throws Exception {
        // given
        AddUserDto addUserDto = AddUserDto.builder()
                .email("jaeyeong@moti.com")
                .pw("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        User user1 = new User(addUserDto.getEmail(), addUserDto.getPw(), addUserDto.getName(), addUserDto.getJob());
        User user2 = new User(addUserDto.getEmail(), addUserDto.getPw(), addUserDto.getName(), addUserDto.getJob());

        // when, then
        userService.join(user1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> userService.join(user2));

        Assertions.assertThat(e.getMessage()).isEqualTo("이미 사용중인 이메일입니다.");
    }
}