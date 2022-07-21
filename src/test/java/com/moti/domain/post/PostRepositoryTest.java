package com.moti.domain.post;

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
class PostRepositoryTest {

    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;

    Post post;
    User user;

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

        em.persist(user);
    }

    @Test
    @DisplayName("포스트 저장")
    void save() throws Exception {
        // when
        Long postId = postRepository.save(post);

        // then
        assertThat(postId).isEqualTo(post.getId());
    }

    @Test
    @DisplayName("포스트 조회")
    void findOne() throws Exception {
        // given
        em.persist(post);

        // when
        Post findPost = postRepository.findOne(post.getId());

        // then
        assertThat(findPost).isEqualTo(post);
    }

    @Test
    @DisplayName("모든 포스트 조회")
    void findAll() throws Exception {
        // given
        Post post2 = Post.builder()
                .title("제목2")
                .content("내용2")
                .user(user)
                .build();

        em.persist(post);
        em.persist(post2);

        // when
        List<Post> posts = postRepository.findAll();

        // then
        assertThat(posts.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("포스트 삭제")
    void delete() throws Exception {
        // given
        em.persist(post);

        // when
        postRepository.delete(post);
        Post findPost = postRepository.findOne(post.getId());

        // then
        assertThat(findPost).isNotEqualTo(post);
    }

}