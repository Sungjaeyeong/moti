package com.moti.web.post;

import com.moti.domain.file.File;
import com.moti.domain.file.FileRepository;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
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
    private final FileRepository fileRepository;

    // 포스트 작성
    @PostMapping()
    public CreatePostResponseDto write(@ModelAttribute @Validated CreatePostDto createPostDto, @SessionAttribute(name = SessionConst.LOGIN_USER) Long sessionUserId) throws IOException {

        Long userId = createPostDto.getUserId();
        User findUser = userService.findOne(userId);

        validateUserSession(sessionUserId, userId);

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
    private void validateUserSession(Long sessionUserId, Long userId) {
        if (!sessionUserId.equals(userId)) {
            log.info("request session LOGIN_USER: {}", sessionUserId);
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
                .map((post -> new PostResponseDto(post)))
                .collect(Collectors.toList());
    }

    // 포스트 수정
    @PatchMapping("/{postId}")
    public void edit(@PathVariable Long postId, @ModelAttribute @Validated EditPostControllerDto editPostControllerDto, @SessionAttribute(name = SessionConst.LOGIN_USER) Long sessionUserId) throws IOException, NotFoundException {
        validateOwnerOfPost(sessionUserId, postId);

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
    public void delete(@PathVariable Long postId, @SessionAttribute(name = SessionConst.LOGIN_USER) Long sessionUserId) throws NotFoundException {
        validateOwnerOfPost(sessionUserId, postId);

        postService.delete(postId);
    }

    public void validateOwnerOfPost(Long sessionUserId, Long postId) {
        Post findPost = postService.findOne(postId);
        Long userId = findPost.getUser().getId();

        if (!userId.equals(sessionUserId)) {
            log.info("request session LOGIN_USER: {}", sessionUserId);
            throw new NotMatchLoginUserSessionException();
        }
    }

    @GetMapping("/attach/{fileId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long fileId) throws MalformedURLException {
        File file = fileRepository.findById(fileId);
        String storeFileName = file.getStoreFileName();
        String uploadFileName = file.getUploadFileName();

        UrlResource urlResource = new UrlResource("file:" + fileService.getFullPath(storeFileName));
        String encodeUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodeUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }

}
