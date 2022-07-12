package com.moti.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.user.UserRepository;
import com.moti.domain.user.entity.Job;
import com.moti.web.user.dto.AddUserDto;
import com.moti.web.user.dto.AddUserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원가입 컨트롤러 성공")
    public void succeed_signup() throws Exception {
        // given
        AddUserDto request = AddUserDto.builder()
                .email("jaeyeong@moti.com")
                .pw("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
//                .andDo(print());
    }

    @Test
    @DisplayName("이메일 없으면 실패")
    public void 이메일_없으면_실패() throws Exception {
        // given
        AddUserDto request = AddUserDto.builder()
                .email("")
                .pw("abcdef")
                .name("재영")
                .job(Job.DEV)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.email").value("이메일은 필수 입력값입니다."))
                .andDo(print());
    }
}