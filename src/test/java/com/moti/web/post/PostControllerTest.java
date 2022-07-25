package com.moti.web.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.post.Post;
import com.moti.domain.post.PostRepository;
import com.moti.domain.post.dto.EditPostDto;
import com.moti.domain.user.UserRepository;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.post.dto.CreatePostDto;
import com.moti.web.post.dto.PostResponseDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;

    User user;
    Long userId;
    MockHttpSession session;

    @BeforeEach
    void join() {
        user = User.builder()
                .email("abc@ac.com")
                .password("abcdef")
                .name("이름")
                .job(Job.DEV)
                .build();
        userId = userRepository.save(user);

        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, userId);
    }

    @Nested
    @DisplayName("포스트 작성")
    class Write {
        @Test
        @DisplayName("포스트 작성 성공")
        public void write_success() throws Exception {
            // given
            CreatePostDto createPostDto = CreatePostDto.builder()
                    .title("제목")
                    .content("내용")
                    .userId(userId)
                    .build();

            String json = objectMapper.writeValueAsString(createPostDto);

            // when
            mockMvc.perform(post("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            assertThat(1L).isEqualTo(postRepository.count());

        }

        @Test
        @DisplayName("포스트 작성 실패 - 다른 유저아이디")
        public void write_fail() throws Exception {
            // given
            CreatePostDto createPostDto = CreatePostDto.builder()
                    .title("제목")
                    .content("내용")
                    .userId(userId)
                    .build();

            String json = objectMapper.writeValueAsString(createPostDto);

            MockHttpSession wrongSession = new MockHttpSession();
            wrongSession.setAttribute(SessionConst.LOGIN_USER, userId+1L);

            // when
            mockMvc.perform(post("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(wrongSession)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                    .andDo(print());

        }
    }

    @Nested
    @DisplayName("포스트 조회")
    class Find {
        Long postId;

        @BeforeEach
        void createPost() {
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .user(user)
                    .build();

            postId = postRepository.save(post);
        }

        @Test
        @DisplayName("포스트 한개 조회")
        public void findOne() throws Exception {
            // when
            mockMvc.perform(get("/posts/{postId}", postId)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(postId))
                    .andDo(print());
        }

        @Test
        @DisplayName("포스트 모두 조회")
        public void findAll() throws Exception {
            // given
            Post post2 = Post.builder()
                    .title("제목2")
                    .content("내용2")
                    .user(user)
                    .build();

            postId = postRepository.save(post2);

            // when
            MvcResult mvcResult = mockMvc.perform(get("/posts")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            PostResponseDto[] readValue = objectMapper.readValue(contentAsString, PostResponseDto[].class);
            assertThat(readValue.length).isEqualTo(2);
        }

        @Test
        @DisplayName("포스트 검색")
        public void findSearch() throws Exception {
            // given
            Post post2 = Post.builder()
                    .title("제목2")
                    .content("안녕하세요")
                    .user(user)
                    .build();

            postId = postRepository.save(post2);

            // when
            MvcResult mvcResult = mockMvc.perform(get("/posts")
                            .param("search", "내용")
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            PostResponseDto[] readValue = objectMapper.readValue(contentAsString, PostResponseDto[].class);
            assertThat(readValue.length).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("포스트 수정")
    class Edit {
        Long postId;
        Post post;

        @BeforeEach
        void createPost() {
            post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .user(user)
                    .build();

            postId = postRepository.save(post);
        }

        @Test
        @DisplayName("포스트 수정 성공")
        public void edit_success() throws Exception {
            // given
            EditPostDto editPostDto = EditPostDto.builder()
                    .title("변경 제목")
                    .content("변경 내용")
                    .build();

            String json = objectMapper.writeValueAsString(editPostDto);

            // when
            mockMvc.perform(patch("/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Post findPost = postRepository.findOne(postId);
            assertThat(findPost).isEqualTo(post);
            System.out.println("findPost = " + findPost.getTitle());
        }

        @Test
        @DisplayName("포스트 수정 실패 - 유저아이디 다름")
        public void edit_fail() throws Exception {
            // given
            EditPostDto editPostDto = EditPostDto.builder()
                    .title("변경 제목")
                    .content("변경 내용")
                    .build();

            String json = objectMapper.writeValueAsString(editPostDto);

            MockHttpSession invalidSession = new MockHttpSession();
            invalidSession.setAttribute(SessionConst.LOGIN_USER, userId+1L);

            // when
            mockMvc.perform(patch("/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(invalidSession)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                    .andDo(print());
        }
    }

    @Test
    @DisplayName("포스트 삭제")
    public void deletePost() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();

        Long postId = postRepository.save(post);

        // when
        mockMvc.perform(delete("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Long count = postRepository.count();
        assertThat(count).isEqualTo(0L);
    }
}