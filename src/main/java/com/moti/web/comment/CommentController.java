package com.moti.web.comment;

import com.moti.domain.comment.Comment;
import com.moti.domain.comment.CommentRepository;
import com.moti.domain.comment.CommentService;
import com.moti.domain.post.Post;
import com.moti.domain.post.PostService;
import com.moti.domain.user.UserService;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.comment.dto.*;
import com.moti.web.exception.NotMatchLoginUserSessionException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    // 댓글 작성
    @PostMapping()
    public WriteCommentResponseDto write(@RequestBody @Validated WriteCommentDto writeCommentDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userId = writeCommentDto.getUserId();

        validateUserSession(session, userId);

        User findUser = userService.findOne(userId);
        Post findPost = postService.findOne(writeCommentDto.getPostId());

        Comment comment = Comment.builder()
                .content("댓글 작성")
                .user(findUser)
                .post(findPost)
                .build();

        Long commentId = commentService.write(comment);

        return new WriteCommentResponseDto(commentId);
    }

    // 포스트의 댓글 조회
    @GetMapping()
    public CommentsResponseDto findByPost(@RequestParam(required = false) Long postId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "5") int maxResults) throws NotFoundException {
        if (postId == null) {
            throw new NotFoundException("Not Found");
        }
        Post post = postService.findOne(postId);
        if (post == null) {
            throw new NotFoundException("Not Found");
        }

        int firstIndex = getFirstIndex(page, maxResults);

        List<PostCommentResponseDto> postCommentResponseDtos = commentService.findByPost(post, firstIndex, maxResults)
                .stream()
                .map(comment -> new PostCommentResponseDto(comment))
                .collect(Collectors.toList());

        return CommentsResponseDto.builder()
                .comments(postCommentResponseDtos)
                .count(postCommentResponseDtos.size())
                .build();
    }

    private int getFirstIndex(int page, int maxResults) {
            return page * maxResults - maxResults;
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public void edit(@PathVariable Long commentId, @RequestBody @Validated EditCommentDto editCommentDto, HttpServletRequest request) throws NotFoundException {
        HttpSession session = request.getSession(false);

        validateUserSession(session, editCommentDto.getUserId());
        validateOwnerOfComment(editCommentDto.getUserId(), commentId);

        commentService.edit(commentId, editCommentDto.getComment());
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public void edit(@PathVariable Long commentId, HttpServletRequest request) throws NotFoundException {
        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER);

        validateOwnerOfComment(userId, commentId);

        commentService.remove(commentId);
    }

    // 세션의 유저와 요청 유저가 동일한지 검사
    private void validateUserSession(HttpSession session, Long userId) {
        Object attribute = session.getAttribute(SessionConst.LOGIN_USER);
        if (!attribute.equals(userId)) {
            log.info("request session LOGIN_USER: {}", attribute);
            throw new NotMatchLoginUserSessionException();
        }
    }

    private void validateOwnerOfComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findOne(commentId);
        Long findUserId = comment.getUser().getId();

        if (!userId.equals(findUserId)) {
            log.info("request userId: {}", userId);
            throw new NotMatchLoginUserSessionException();
        }
    }
}
