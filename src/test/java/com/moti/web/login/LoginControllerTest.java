package com.moti.web.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.user.UserService;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.login.dto.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginControllerTest {

    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;

    @Autowired UserService userService;
    User user;

    @BeforeEach
    void join() {
        user = new User("aaa@gmail.com", "abcdef", "user1", Job.DEV);
        userService.join(user);
    }

    @Test
    @DisplayName("로그인 성공 - 컨트롤러")
    public void succeed_login() throws Exception {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .email(user.getEmail())
                .pw(user.getPassword())
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.job").value("DEV"))
                .andExpect(request().sessionAttribute(SessionConst.LOGIN_USER, user.getId()))
                .andExpect(cookie().value(SessionConst.LOGIN_USER, String.valueOf(user.getId())))
                .andDo(print());

    }

    @Test
    @DisplayName("비밀번호 오류 시 로그인 실패 - 컨트롤러")
    public void failed_login() throws Exception {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .email(user.getEmail())
                .pw("aaaaaa")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("유저가 존재하지 않습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("로그아웃 시 세션 초기화")
    public void logout() throws Exception {
        // given
        Long userId = 1L;

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, userId);

        System.out.println("session = " + session.getAttribute(SessionConst.LOGIN_USER));
        // when, then
        mockMvc.perform(post("/logout")
                        .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(request().sessionAttributeDoesNotExist(SessionConst.LOGIN_USER))
                .andDo(print());

    }
}