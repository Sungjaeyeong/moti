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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.moti.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class LoginControllerTest {

    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;

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
    @DisplayName("로그인 성공 - 컨트롤러")
    public void succeed_login() throws Exception {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .email(user.getEmail())
                .password(user.getPassword())
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
                .andDo(print())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("email").description("이메일").attributes(Attributes.key("required").value("O")),
                                fieldWithPath("password").description("비밀번호").attributes(Attributes.key("required").value("O"))
                        ),
                        responseFields(
                                fieldWithPath("id").description("유저 ID"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("name").description("유저 이름"),
                                fieldWithPath("job").description("직업"),
                                fieldWithPath("introduce").description("소개")
                        )
                ));

    }

    @Test
    @DisplayName("로그인 시 이메일은 필수 값 입니다.")
    public void login_requiredEmail() throws Exception {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .email("")
                .password("aaaaa")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.email").value("이메일은 필수 입력값입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("비밀번호 오류 시 로그인 실패 - 컨트롤러")
    public void failed_login() throws Exception {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .email(user.getEmail())
                .password("aaaaaa")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못되었습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("로그아웃 시 세션 초기화")
    public void logout() throws Exception {
        // given
        Long userId = 1L;

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, userId);

        // when, then
        mockMvc.perform(post("/logout")
                        .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(request().sessionAttributeDoesNotExist(SessionConst.LOGIN_USER))
                .andDo(print())
                .andDo(document("logout",
                        responseBody()
                ));

    }
}