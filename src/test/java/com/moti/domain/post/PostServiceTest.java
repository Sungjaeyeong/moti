package com.moti.domain.post;

import com.moti.domain.post.dto.EditPostServiceDto;
import com.moti.domain.user.UserRepository;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired PostRepository postRepository;
    @Autowired UserRepository userRepository;
    @Autowired PostService postService;

    User user;
    Post post;
    Long postId;

    @BeforeEach
    void beforeEach() {
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

        userRepository.save(user);
        postId = postRepository.save(post);
    }

    @Test
    @DisplayName("포스트 작성")
    public void write() throws Exception {
        // given
        Post post2 = Post.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();

        // when
        Long postId2 = postService.write(post2);

        // then
        assertThat(post2).isEqualTo(postRepository.findOne(postId2));
    }

    @Test
    @DisplayName("포스트 한개 조회")
    public void findOne() throws Exception {
        // when
        Post findPost = postService.findOne(postId);

        // then
        assertThat(post).isEqualTo(findPost);
    }

    @Test
    @DisplayName("모든 포스트 조회")
    public void findAll() throws Exception {
        // when
        List<Post> postList = postService.findAll();

        // then
        assertThat(postList.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("포스트 수정")
    public void edit() throws Exception {
        // given
        EditPostServiceDto editPostServiceDto = EditPostServiceDto.builder()
                .title("변경 제목")
                .content("변경 내용")
                .build();

        // when
        postService.edit(postId, editPostServiceDto);

        // then
        Post findPost = postRepository.findOne(postId);
        assertThat(findPost.getTitle()).isEqualTo("변경 제목");
        assertThat(findPost.getContent()).isEqualTo("변경 내용");
    }

    @Test
    @DisplayName("포스트 삭제")
    public void delete() throws Exception {
        // when
        postService.delete(postId);

        // then
        Post findPost = postRepository.findOne(postId);
        assertThat(findPost).isEqualTo(null);
    }

    @Test
    @DisplayName("없는 포스트 삭제")
    public void delete_fail() throws Exception {
        // when, then
        assertThrows(NotFoundException.class, () -> postService.delete(postId+1L));
    }

    @Test
    @DisplayName("포스트 검색")
    public void search() throws Exception {
        // given
        Post post = Post.builder()
                .user(user)
                .title("제목")
                .content("안녕하세요!")
                .build();

        postRepository.save(post);

        // when
        List<Post> posts = postService.findSearch("안녕하세요");

        // then
        assertThat(posts.size()).isEqualTo(1);
    }
}