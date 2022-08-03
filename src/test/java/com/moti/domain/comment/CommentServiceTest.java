package com.moti.domain.comment;

import com.moti.domain.post.Post;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired EntityManager em;
    @Autowired CommentService commentService;

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
    @DisplayName("댓글 작성")
    public void write() throws Exception {
        // given
        Comment comment2 = Comment.builder()
                .content("댓글2")
                .post(post)
                .user(user)
                .build();

        // when
        Long commentId = commentService.write(comment2);

        // then
        Assertions.assertThat(comment2).isEqualTo(em.find(Comment.class, commentId));
    }

    @Test
    @DisplayName("포스트의 댓글 조회")
    public void findByPost() throws Exception {
        // when
        List<Comment> comments = commentService.findByPost(post);

        // then
        Assertions.assertThat(comments.size()).isEqualTo(1);
        Assertions.assertThat(comments.get(0).getContent()).isEqualTo("댓글");
    }

    @Test
    @DisplayName("댓글 삭제")
    public void remove() throws Exception {
        // when
        commentService.remove(comment.getId());

        // then
        List<Comment> comments = commentService.findByPost(post);
        Assertions.assertThat(comments.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 수정")
    public void edit() throws Exception {
        // when
        commentService.edit(comment.getId(), "댓글 수정됨");

        // then
        Comment findComment = em.find(Comment.class, this.comment.getId());
        Assertions.assertThat(findComment.getContent()).isEqualTo("댓글 수정됨");
    }

}