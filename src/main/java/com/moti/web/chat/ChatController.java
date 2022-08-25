package com.moti.web.chat;

import com.moti.domain.chat.ChatService;
import com.moti.domain.chat.entity.Chat;
import com.moti.web.SessionConst;
import com.moti.web.chat.dto.*;
import com.moti.web.exception.NotMatchLoginUserSessionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    // 채팅 생성
    @PostMapping
    public CreateChatResponseDto createChat(@RequestBody @Validated CreateChatDto createChatDto, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        validateSession(session, createChatDto.getUserIds());

        Chat chat = chatService.createChat(createChatDto.getUserIds());

        return new CreateChatResponseDto(chat.getId());
    }

    // 유저가 속한 채팅 조회
    @GetMapping()
    public ResponseChatsDto findUserChat(@RequestParam Long userId, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        validateUserSession(session, userId);

        List<Chat> chats = chatService.findChatsByUser(userId);
        List<ResponseChatDto> responseChatDtos = chats.stream().map(chat -> ResponseChatDto.builder()
                .chatId(chat.getId())
                .chatName(chat.getName())
                .userCount(chat.getChatUsers().size())
                .createdAt(chat.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .updatedAt(chat.getLastModifiedDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .build()
        ).collect(Collectors.toList());

        return ResponseChatsDto.builder()
                .chats(responseChatDtos)
                .count(responseChatDtos.size())
                .build();
    }

    // 세션의 유저와 요청 유저가 동일한지 검사
    private void validateUserSession(HttpSession session, Long userId) {
        Object attribute = session.getAttribute(SessionConst.LOGIN_USER);
        if (!attribute.equals(userId)) {
            log.info("request session LOGIN_USER: {}", attribute);
            throw new NotMatchLoginUserSessionException();
        }
    }

    // 특정 채팅 조회 (메세지 필요)

    // 채팅 초대
    @PostMapping("/update/user")
    public void inviteChat(@RequestBody @Validated InviteChatDto inviteChatDto, HttpServletRequest request) {
        Chat chat = chatService.findChat(inviteChatDto.getChatId());
        List<Long> userIds = chat.getChatUsers().stream().map(chatUser -> chatUser.getUser().getId())
                .collect(Collectors.toList());

        HttpSession session = request.getSession(false);
        validateSession(session, userIds);

        chatService.inviteChat(inviteChatDto.getChatId(), inviteChatDto.getUserId());
    }

    // 채팅 나가기
    @PostMapping("/delete/user")
    public void exitChat(@RequestBody @Validated ExitChatDto exitChatDto, HttpServletRequest request) {
        Chat chat = chatService.findChat(exitChatDto.getChatId());
        List<Long> userIds = chat.getChatUsers().stream().map(chatUser -> chatUser.getUser().getId())
                .collect(Collectors.toList());

        HttpSession session = request.getSession(false);
        validateSession(session, userIds);

        chatService.exitChat(exitChatDto.getChatId(), exitChatDto.getUserId());
    }

    // 채팅방 이름 변경
    @PostMapping("/update/name")
    public void changeChatName(@RequestBody @Validated ChangeChatNameDto changeChatNameDto, HttpServletRequest request) {
        Chat chat = chatService.findChat(changeChatNameDto.getChatId());
        List<Long> userIds = chat.getChatUsers().stream().map(chatUser -> chatUser.getUser().getId())
                .collect(Collectors.toList());

        HttpSession session = request.getSession(false);
        validateSession(session, userIds);

        chatService.changeChatName(changeChatNameDto.getChatId(), changeChatNameDto.getName());
    }

    private void validateSession(HttpSession session, List<Long> userIds) {
        Long sessionUserId = (Long) session.getAttribute(SessionConst.LOGIN_USER);

        if (!userIds.contains(sessionUserId)) {
            log.info("request session LOGIN_USER: {}", sessionUserId);
            throw new NotMatchLoginUserSessionException();
        }
    }
}
