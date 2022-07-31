package com.moti.domain.comment;

import com.moti.domain.post.Post;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentRepositoryTest {

    @Autowired CommentRepository commentRepository;
    @Autowired EntityManager em;

    User user;
    Post post;
    Comment comment;

    @BeforeEach
    void setup() {
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
    }

    @Test
    @DisplayName("댓글 저장")
    public void save() throws Exception {
        //given
        Comment comment2 = Comment.builder()
                .content("댓글2")
                .post(post)
                .user(user)
                .build();

        // when
        Long commentId = commentRepository.save(comment2);

        // then
        assertThat(commentId).isEqualTo(comment2.getId());
    }

    @Test
    @DisplayName("댓글 한개 조회")
    public void findOne() throws Exception {
        // when
        Comment findComment = commentRepository.findOne(comment.getId());

        // then
        assertThat(findComment).isEqualTo(comment);
    }

    @Test
    @DisplayName("댓글 모두 조회")
    public void findAll() throws Exception {
        // given
        Comment comment2 = Comment.builder()
                .content("댓글2")
                .post(post)
                .user(user)
                .build();

        Comment comment3 = Comment.builder()
                .content("댓글3")
                .post(post)
                .user(user)
                .build();

        em.persist(comment2);
        em.persist(comment3);

        // when
        List<Comment> comments = commentRepository.findAll();

        // then
        assertThat(comments.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("댓글 삭제")
    public void delete() throws Exception {
        // when
        commentRepository.delete(comment);

        // then
        Comment findComment = em.find(Comment.class, this.comment.getId());
        assertThat(findComment).isEqualTo(null);
    }

    @Test
    @DisplayName("포스트로 댓글 조회")
    public void findByPostId() throws Exception {
        // given
        Comment comment2 = Comment.builder()
                .content("댓글2")
                .post(post)
                .user(user)
                .build();

        em.persist(comment2);

        // when
        List<Comment> comments = commentRepository.findByPost(post);

        // then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).getContent()).isEqualTo("댓글2");
    }
}