package com.moti.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.user.UserRepository;
import com.moti.domain.user.dto.EditUserDto;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.user.dto.AddUserDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;

    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("회원가입 컨트롤러 성공")
    public void succeed_signup() throws Exception {
        // given
        AddUserDto request = AddUserDto.builder()
                .email("jaeyeong@moti.com")
                .password("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk());
//                .andDo(print());

        Assertions.assertThat(1L).isEqualTo(userRepository.count());
    }

    @Test
    @DisplayName("회원가입시 이메일 없으면 실패")
    public void 이메일_없으면_실패() throws Exception {
        // given
        AddUserDto request = AddUserDto.builder()
                .email("")
                .password("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.email").value("이메일은 필수 입력값입니다."))
                .andDo(print());
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

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, userId);

        // when, then
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.job").value("DEV"))
                .andExpect(jsonPath("$.introduce").value(user.getIntroduce()))
                .andDo(print());

    }

    @Nested
    @DisplayName("유저 정보 수정")
    class edit {
        Long userId;
        MockHttpSession session;

        @BeforeEach
        void join() {
            User user = User.builder()
                    .email("abc@ac.com")
                    .password("abcdef")
                    .name("이름")
                    .job(Job.DEV)
                    .build();
            userId = userRepository.save(user);

            session = new MockHttpSession();
            session.setAttribute(SessionConst.LOGIN_USER, userId);

        }

        @Test
        @DisplayName("유저 정보 수정 성공")
        public void editUser() throws Exception {
            // given
            EditUserDto editUserDto = EditUserDto.builder()
                    .name("변경후이름")
                    .introduce("변경후소개")
                    .build();

            String json = objectMapper.writeValueAsString(editUserDto);

            // when, then
            mockMvc.perform(patch("/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("유저 정보 수정 실패 - 이름없음")
        public void editUser_fail() throws Exception {
            // given
            EditUserDto editUserDto = EditUserDto.builder()
                    .introduce("변경후소개")
                    .build();

            String json = objectMapper.writeValueAsString(editUserDto);

            // when, then
            mockMvc.perform(patch("/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.validation.name").value("이름은 필수 입력값입니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("유저 정보 수정 실패 - 소개없음")
        public void editUser_fail2() throws Exception {
            // given
            EditUserDto editUserDto = EditUserDto.builder()
                    .name("변경이름")
                    .build();

            String json = objectMapper.writeValueAsString(editUserDto);

            // when, then
            mockMvc.perform(patch("/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.validation.introduce").value("소개는 필수 입력값입니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("유저 정보 수정 실패 - 세션다름")
        public void editUser_fail3() throws Exception {
            // given
            EditUserDto editUserDto = EditUserDto.builder()
                    .introduce("변경소개")
                    .name("변경이름")
                    .build();

            String json = objectMapper.writeValueAsString(editUserDto);

            MockHttpSession invalidSession = new MockHttpSession();
            invalidSession.setAttribute(SessionConst.LOGIN_USER, 2L);

            // when, then
            mockMvc.perform(patch("/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(invalidSession))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                    .andDo(print());
        }
    }

}