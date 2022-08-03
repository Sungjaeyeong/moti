package com.moti.domain.comment;

import com.moti.domain.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    // 댓글 작성
    public Long write(Comment comment) {
        return commentRepository.save(comment);
    }

    // 특정 포스트 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> findByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    // 댓글 삭제
    public void remove(Comment comment) {
        commentRepository.delete(comment);
    }

    // 댓글 수정
    public void edit(Long commentId, String content) {
        Comment findComment = commentRepository.findOne(commentId);
        findComment.changeComment(content);
    }
}
