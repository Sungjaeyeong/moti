package com.moti.web.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.post.Post;
import com.moti.domain.post.PostRepository;
import com.moti.domain.user.UserRepository;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.post.dto.CreatePostDto;
import com.moti.web.post.dto.CreatePostResponseDto;
import com.moti.web.post.dto.EditPostControllerDto;
import com.moti.web.post.dto.PostResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

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
    @Autowired EntityManager em;

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
            MockMultipartFile image1 = new MockMultipartFile("multipartFiles", "imagefile1.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());
            MockMultipartFile image2 = new MockMultipartFile("multipartFiles", "imagefile2.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());

            CreatePostDto createPostDto = CreatePostDto.builder()
                    .title("제목")
                    .content("내용")
                    .userId(userId)
                    .build();

            // when
            mockMvc.perform(multipart("/posts")
                            .file(image1)
                            .file(image2)
                            .param("title", createPostDto.getTitle())
                            .param("content", createPostDto.getContent())
                            .param("userId", String.valueOf(createPostDto.getUserId()))
                            .session(session)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Long fileCount = em.createQuery("select count(f) from File f", Long.class)
                    .getSingleResult();

            assertThat(1L).isEqualTo(postRepository.count());
            assertThat(2L).isEqualTo(fileCount);

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

            MockHttpSession wrongSession = new MockHttpSession();
            wrongSession.setAttribute(SessionConst.LOGIN_USER, userId+1L);

            // when
            mockMvc.perform(post("/posts")
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .param("title", createPostDto.getTitle())
                            .param("content", createPostDto.getContent())
                            .param("userId", String.valueOf(createPostDto.getUserId()))
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
            MockMultipartFile oldImage1 = new MockMultipartFile("multipartFiles", "oldimagefile1.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());
            MockMultipartFile oldImage2 = new MockMultipartFile("multipartFiles", "oldimagefile2.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());

            CreatePostDto createPostDto = CreatePostDto.builder()
                    .title("제목")
                    .content("내용")
                    .userId(userId)
                    .build();

            MvcResult mvcResult = mockMvc.perform(multipart("/posts")
                    .file(oldImage1)
                    .file(oldImage2)
                    .param("title", createPostDto.getTitle())
                    .param("content", createPostDto.getContent())
                    .param("userId", String.valueOf(createPostDto.getUserId()))
                    .session(session)
            ).andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            CreatePostResponseDto readValue = objectMapper.readValue(contentAsString, CreatePostResponseDto.class);
            Long createdPostId = readValue.getId();

            MockMultipartFile image1 = new MockMultipartFile("multipartFiles", "imagefile1.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());

            EditPostControllerDto editPostControllerDto = EditPostControllerDto.builder()
                    .title("변경 제목")
                    .content("변경 내용")
                    .build();

            MockMultipartHttpServletRequestBuilder builder =
                    MockMvcRequestBuilders.multipart("/posts/{postId}", createdPostId);
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod("PATCH");
                    return request;
                }
            });

            // when
            mockMvc.perform(builder
                            .file(image1)
                            .param("title", editPostControllerDto.getTitle())
                            .param("content", editPostControllerDto.getContent())
                            .session(session)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Post findPost = postRepository.findOne(createdPostId);
            Long fileCount = em.createQuery("select count(f) from File f", Long.class)
                    .getSingleResult();

            assertThat(findPost.getTitle()).isEqualTo("변경 제목");
            assertThat(fileCount).isEqualTo(1L);
        }

        @Test
        @DisplayName("포스트 수정 실패 - 유저아이디 다름")
        public void edit_fail() throws Exception {
            // given
            EditPostControllerDto editPostControllerDto = EditPostControllerDto.builder()
                    .title("변경 제목")
                    .content("변경 내용")
                    .build();

            MockHttpSession invalidSession = new MockHttpSession();
            invalidSession.setAttribute(SessionConst.LOGIN_USER, userId+1L);

            // when
            mockMvc.perform(patch("/posts/{postId}", postId)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .param("title", editPostControllerDto.getTitle())
                            .param("content", editPostControllerDto.getContent())
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