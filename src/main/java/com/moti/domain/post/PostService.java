package com.moti.domain.post;

import com.moti.domain.post.dto.EditPostServiceDto;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    // 포스트 작성
    @Transactional
    public Long write(Post post) {
        return postRepository.save(post);
    }

    // 포스트 한개 조회
    public Post findOne(Long postId) {
        return postRepository.findOne(postId);
    }

    // 모든 포스트 조회
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    // 포스트 수정
    @Transactional
    public void edit(Long postId, EditPostServiceDto editPostServiceDto) throws NotFoundException {
        Post findPost = postRepository.findOne(postId);
        if (findPost == null) {
            throw new NotFoundException("Not found");
        }
        findPost.changePost(editPostServiceDto);
    }

    // 포스트 삭제
    @Transactional
    public void delete(Long postId) throws NotFoundException {
        Post findPost = postRepository.findOne(postId);
        if (findPost == null) {
            throw new NotFoundException("Not found");
        }
        postRepository.delete(findPost);
    }

    // 포스트 검색
    public List<Post> findSearch(String searchWord) {
        return postRepository.findSearch(searchWord);
    }
}
