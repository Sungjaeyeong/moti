package com.moti.domain.comment;

import com.moti.domain.post.Post;
import javassist.NotFoundException;
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
    public List<Comment> findByPost(Post post, Integer firstIndex, Integer maxResults) {
        if (firstIndex == null) firstIndex = 0;
        if (maxResults == null) maxResults = 5;
        return commentRepository.findByPostWithUser(post, firstIndex, maxResults);
    }

    // 댓글 수정
    public void edit(Long commentId, String content) throws NotFoundException {
        Comment findComment = commentRepository.findOne(commentId);
        if (findComment == null) {
            throw new NotFoundException("Not found");
        }
        findComment.changeComment(content);
    }

    // 댓글 삭제
    public void remove(Long commentId) throws NotFoundException {
        Comment findComment = commentRepository.findOne(commentId);
        if (findComment == null) {
            throw new NotFoundException("Not found");
        }
        commentRepository.delete(findComment);
    }
}
