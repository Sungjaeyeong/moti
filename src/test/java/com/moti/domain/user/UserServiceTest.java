package com.moti.domain.user;

import com.moti.domain.user.dto.EditUserDto;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.domain.exception.NotFoundUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
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
        User user = User.builder()
                .email("jaeyeong@moti.com")
                .password("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        // when
        Long joinUserId = userService.join(user);

        // then
        assertThat(user).isEqualTo(userRepository.findOne(joinUserId));
    }

    @Test
    @DisplayName("중복 회원 예외")
    public void validate_user() throws Exception {
        // given
        User user1 = User.builder()
                .email("jaeyeong@moti.com")
                .password("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        User user2 = User.builder()
                .email("jaeyeong@moti.com")
                .password("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        // when, then
        userService.join(user1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> userService.join(user2));

        assertThat(e.getMessage()).isEqualTo("이미 사용중인 이메일입니다.");
    }

    @Test
    @DisplayName("유저 한명 조회")
    public void findOne() throws Exception {
        // given
        User user = User.builder()
                .email("abc@ac.com")
                .password("abcdef")
                .name("이름")
                .job(Job.DEV)
                .build();
        Long userId = userRepository.save(user);

        // when
        User savedUser = userService.findOne(userId);

        // then
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    @DisplayName("없는 유저 조회")
    public void notExistsUser() throws Exception {
        // given
        User user = User.builder()
                .email("abc@ac.com")
                .password("abcdef")
                .name("이름")
                .job(Job.DEV)
                .build();
        Long userId = userRepository.save(user);

        // when, then
        assertThrows(NotFoundUserException.class, () -> userService.findOne(userId + 1L));

    }

    @Test
    @DisplayName("유저 정보 수정")
    public void editUser() throws Exception {
        // given
        User user = User.builder()
                .email("abc@ac.com")
                .password("abcdef")
                .name("이름")
                .job(Job.DEV)
                .build();
        Long userId = userRepository.save(user);

        EditUserDto editUserDto = EditUserDto.builder()
                .name("변경후이름")
                .introduce("변경후소개")
                .build();

        // when
        userService.edit(userId, editUserDto);

        // then
        User findUser = userRepository.findOne(userId);
        assertThat(findUser.getName()).isEqualTo(editUserDto.getName());
        assertThat(findUser.getIntroduce()).isEqualTo(editUserDto.getIntroduce());
    }

}