package com.moti.web.chat;

import com.moti.domain.chat.ChatService;
import com.moti.domain.chat.entity.Chat;
import com.moti.domain.message.Message;
import com.moti.domain.message.MessageService;
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
    private final MessageService messageService;

    // 채팅 생성
    @PostMapping
    public CreateChatResponseDto createChat(@RequestBody @Validated CreateChatDto createChatDto, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        Long sessionUserId = (Long) session.getAttribute(SessionConst.LOGIN_USER);

        if (!createChatDto.getUserIds().contains(sessionUserId)) {
            log.info("request session LOGIN_USER: {}", sessionUserId);
            throw new NotMatchLoginUserSessionException();
        }

        Chat chat = chatService.createChat(createChatDto.getUserIds());

        return new CreateChatResponseDto(chat.getId());
    }

    // 유저가 속한 채팅들 조회
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

    // 특정 채팅 조회 (메세지 포함)
    @GetMapping("{chatId}")
    public ResponseChatWithMessageDto findChat(@PathVariable Long chatId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        validateSession(session, chatId);

        Chat chat = chatService.findChat(chatId);

        ResponseChatDto responseChatDto = ResponseChatDto.builder()
                .chatId(chat.getId())
                .chatName(chat.getName())
                .userCount(chat.getChatUsers().size())
                .createdAt(chat.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .updatedAt(chat.getLastModifiedDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .build();

        List<Message> messages = messageService.findMessagesByChat(chatId);

        return ResponseChatWithMessageDto.builder()
                .responseChatDto(responseChatDto)
                .messages(messages)
                .build();
    }

    // 채팅 초대
    @PostMapping("/update/user")
    public void inviteChat(@RequestBody @Validated InviteChatDto inviteChatDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        validateSession(session, inviteChatDto.getChatId());

        chatService.inviteChat(inviteChatDto.getChatId(), inviteChatDto.getUserId());
    }

    // 채팅 나가기
    @PostMapping("/delete/user")
    public void exitChat(@RequestBody @Validated ExitChatDto exitChatDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        validateSession(session, exitChatDto.getChatId());

        chatService.exitChat(exitChatDto.getChatId(), exitChatDto.getUserId());
    }

    // 채팅방 이름 변경
    @PostMapping("/update/name")
    public void changeChatName(@RequestBody @Validated ChangeChatNameDto changeChatNameDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        validateSession(session, changeChatNameDto.getChatId());

        chatService.changeChatName(changeChatNameDto.getChatId(), changeChatNameDto.getName());
    }

    private void validateSession(HttpSession session, Long chatId) {
        Chat chat = chatService.findChat(chatId);
        List<Long> userIds = chat.getChatUsers().stream().map(chatUser -> chatUser.getUser().getId())
                .collect(Collectors.toList());

        Long sessionUserId = (Long) session.getAttribute(SessionConst.LOGIN_USER);

        if (!userIds.contains(sessionUserId)) {
            log.info("request session LOGIN_USER: {}", sessionUserId);
            throw new NotMatchLoginUserSessionException();
        }
    }
}
