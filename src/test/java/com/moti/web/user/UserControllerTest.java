package com.moti.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.user.UserRepository;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.user.dto.AddUserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @DisplayName("회원가입시 이메일 없으면 실패")
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

        // when, then
        mockMvc.perform(get("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.job").value("DEV"))
                .andExpect(jsonPath("$.introduce").value(user.getIntroduce()))
                .andDo(print());

    }
}