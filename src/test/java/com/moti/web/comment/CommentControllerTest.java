package com.moti.web.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.comment.Comment;
import com.moti.domain.post.Post;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.comment.dto.CommentsResponseDto;
import com.moti.web.comment.dto.EditCommentDto;
import com.moti.web.comment.dto.WriteCommentDto;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;

    User user;
    Post post;
    Comment comment;
    MockHttpSession session;

    @BeforeEach
    void init() {
        user = User.builder()
                .email("wodud@afd.com")
                .password("abcdef")
                .name("wodud")
                .job(Job.DEV)
                .build();

        post = Post.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();

        comment = Comment.builder()
                .content("댓글")
                .post(post)
                .user(user)
                .build();

        em.persist(user);
        em.persist(post);
        em.persist(comment);

        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, user.getId());
    }

    @Nested
    @DisplayName("댓글 작성")
    class Write {
        @Test
        @DisplayName("댓글 작성 성공")
        public void write_success() throws Exception {
            // given
            WriteCommentDto writeCommentDto = WriteCommentDto.builder()
                    .content("댓글씀")
                    .postId(post.getId())
                    .userId(user.getId())
                    .build();

            String json = objectMapper.writeValueAsString(writeCommentDto);

            // when
            mockMvc.perform(post("/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .session(session)
            )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(em.createQuery("select count(c) from Comment c", Long.class).getSingleResult()).isEqualTo(2L);
        }

        @Test
        @DisplayName("댓글 작성 실패 - 다른 유저아이디")
        public void write_fail() throws Exception {
            // given
            WriteCommentDto writeCommentDto = WriteCommentDto.builder()
                    .content("댓글씀")
                    .postId(post.getId())
                    .userId(user.getId())
                    .build();

            String json = objectMapper.writeValueAsString(writeCommentDto);

            MockHttpSession wrongSession = new MockHttpSession();
            wrongSession.setAttribute(SessionConst.LOGIN_USER, user.getId()+1L);

            // when
            mockMvc.perform(post("/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(wrongSession)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                    .andDo(print());

            // then
            assertThat(em.createQuery("select count(c) from Comment c", Long.class).getSingleResult()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("댓글 조회")
    class find {
        @Test
        @DisplayName("댓글 조회1")
        public void find1() throws Exception {
            // given
            for (int i=1; i<21; i++) {
                Comment comment = Comment.builder()
                        .content("comment" + i)
                        .post(post)
                        .user(user)
                        .build();
                em.persist(comment);
            }

            // when
            MvcResult mvcResult = mockMvc.perform(get("/comments")
                            .param("postId", String.valueOf(post.getId()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(5))
                    .andDo(print())
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            CommentsResponseDto response = objectMapper.readValue(contentAsString, CommentsResponseDto.class);
            assertThat(response.getComments().get(0).getContent()).isEqualTo("comment20");
        }

        @Test
        @DisplayName("댓글 조회2")
        public void find2() throws Exception {
            // given
            for (int i=1; i<21; i++) {
                Comment comment = Comment.builder()
                        .content("comment" + i)
                        .post(post)
                        .user(user)
                        .build();
                em.persist(comment);
            }

            // when
            MvcResult mvcResult = mockMvc.perform(get("/comments")
                            .param("postId", String.valueOf(post.getId()))
                            .param("page", String.valueOf(2))
                            .param("maxResults", String.valueOf(10))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(10))
                    .andDo(print())
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString();
            CommentsResponseDto response = objectMapper.readValue(contentAsString, CommentsResponseDto.class);
            assertThat(response.getComments().get(0).getContent()).isEqualTo("comment10");
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class edit {
        @Test
        @DisplayName("댓글 수정 성공")
        public void edit_success() throws Exception {
            // given
            EditCommentDto editCommentDto = EditCommentDto.builder()
                    .comment("변경 댓글")
                    .userId(user.getId())
                    .build();

            String json = objectMapper.writeValueAsString(editCommentDto);

            // when
            mockMvc.perform(patch("/comments/{commentId}", comment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session)
                    )
                    .andDo(print());

            // then
            Comment findComment = em.find(Comment.class, CommentControllerTest.this.comment.getId());
            assertThat(findComment.getContent()).isEqualTo("변경 댓글");
        }

        @Test
        @DisplayName("댓글 수정 실패 - 다른 유저의 댓글")
        public void edit_failed() throws Exception {
            // given
            EditCommentDto editCommentDto = EditCommentDto.builder()
                    .comment("변경 댓글")
                    .userId(user.getId()+1)
                    .build();

            String json = objectMapper.writeValueAsString(editCommentDto);

            // when
            mockMvc.perform(patch("/comments/{commentId}", comment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session)
                    )
                    .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                    .andDo(print());

            // then
            Comment findComment = em.find(Comment.class, comment.getId());
            assertThat(findComment.getContent()).isEqualTo("댓글");
        }
    }

    @Test
    @DisplayName("댓글 삭제")
    public void delete() throws Exception {
        // when
        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{commentId}", comment.getId())
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Comment findComment = em.find(Comment.class, comment.getId());
        assertThat(findComment).isEqualTo(null);
    }
}