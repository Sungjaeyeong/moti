package com.moti.web.post;

import com.moti.domain.file.File;
import com.moti.domain.file.FileService;
import com.moti.domain.post.Post;
import com.moti.domain.post.PostService;
import com.moti.domain.post.dto.EditPostServiceDto;
import com.moti.domain.user.UserService;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.exception.NotMatchLoginUserSessionException;
import com.moti.web.post.dto.CreatePostDto;
import com.moti.web.post.dto.CreatePostResponseDto;
import com.moti.web.post.dto.EditPostControllerDto;
import com.moti.web.post.dto.PostResponseDto;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final FileService fileService;

    // 포스트 작성
    @PostMapping()
    public CreatePostResponseDto write(@RequestBody @Validated CreatePostDto createPostDto, HttpServletRequest request) throws IOException {

        HttpSession session = request.getSession(false);
        Long userId = createPostDto.getUserId();
        User findUser = userService.findOne(userId);

        validateUserSession(session, userId);

        List<File> storeFiles = fileService.storeFiles(createPostDto.getMultipartFiles());

        Post post = Post.builder()
                .title(createPostDto.getTitle())
                .content(createPostDto.getContent())
                .user(findUser)
                .files(storeFiles)
                .build();

        Long postId = postService.write(post);

        return new CreatePostResponseDto(postId);
    }

    // 세션의 유저와 요청 유저가 동일한지 검사
    private void validateUserSession(HttpSession session, Long userId) {
        Object attribute = session.getAttribute(SessionConst.LOGIN_USER);
        if (!attribute.equals(userId)) {
            log.info("request session LOGIN_USER: {}", attribute);
            throw new NotMatchLoginUserSessionException();
        }
    }

    // 포스트 한개 조회
    @GetMapping("/{postId}")
    public PostResponseDto findOne(@PathVariable Long postId) {
        Post findPost = postService.findOne(postId);

        return new PostResponseDto(findPost);
    }

    // 모든 포스트 조회
    @GetMapping()
    public List<PostResponseDto> findAll(@RequestParam(value = "search", required = false) String searchWord) {
        List<Post> PostList = postService.findAll();

        if (searchWord != null) {
            PostList = postService.findSearch(searchWord);
        }

        return PostList.stream()
                .map((post -> new PostResponseDto(post))
                )
                .collect(Collectors.toList());
    }

    // 포스트 수정
    @PatchMapping("/{postId}")
    public void edit(@PathVariable Long postId, @ModelAttribute @Validated EditPostControllerDto editPostControllerDto, HttpServletRequest request) throws IOException, NotFoundException {
        HttpSession session = request.getSession(false);
        validateOwnerOfPost(session, postId);

        List<File> storeFiles = fileService.storeFiles(editPostControllerDto.getMultipartFiles());

        EditPostServiceDto editPostServiceDto = EditPostServiceDto.builder()
                .title(editPostControllerDto.getTitle())
                .content(editPostControllerDto.getContent())
                .fileList(storeFiles)
                .build();

        postService.edit(postId, editPostServiceDto);

    }

    // 포스트 삭제
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable Long postId, HttpServletRequest request) throws NotFoundException {
        HttpSession session = request.getSession(false);
        validateOwnerOfPost(session, postId);

        postService.delete(postId);
    }

    public void validateOwnerOfPost(HttpSession session, Long postId) {
        Long sessionUserId = (Long) session.getAttribute(SessionConst.LOGIN_USER);

        Post findPost = postService.findOne(postId);
        Long userId = findPost.getUser().getId();

        if (!userId.equals(sessionUserId)) {
            log.info("request session LOGIN_USER: {}", sessionUserId);
            throw new NotMatchLoginUserSessionException();
        }
    }

}
