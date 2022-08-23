package com.moti.domain.chat;

import com.moti.domain.chat.entity.Chat;
import com.moti.domain.chat.entity.ChatUser;
import com.moti.domain.exception.DuplicateChatException;
import com.moti.domain.user.UserService;
import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;

    // 채팅 생성
    public Chat createChat(List<User> users) {
        if (users.size() < 2) {
            throw new IllegalStateException("인원이 부족합니다.");
        }
        validateDuplicateUsers(users);
        List<ChatUser> chatUsers = users.stream().map(user -> ChatUser.createChatUser(user))
                .collect(Collectors.toList());

        Chat chat = Chat.createChat(chatUsers);
        chatRepository.save(chat);

        return chat;
    }

    private void validateDuplicateUsers(List<User> users) {
        if (users.size() == 2) {
            Chat chat = chatRepository.findByUsers(users);
            if (chat != null) {
                throw new DuplicateChatException("이미 채팅방이 존재합니다.");
            }
        }
    }

    // 유저가 속한 채팅 조회
    @Transactional(readOnly = true)
    public List<Chat> findChatsByUser(Long userId) {
        return chatRepository.findChatsByUser(userId);
    }

    // 채팅 조회
    @Transactional(readOnly = true)
    public Chat findChat(Long chatId) {
        return chatRepository.findWithUser(chatId);
    }

    // 채팅방 이름 변경
    public void changeChatName(Long chatId, String name) {
        Chat chat = chatRepository.findOne(chatId);

        chat.changeChatName(name);
    }

    // 채팅 초대
    public void inviteChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findOne(chatId);
        User user = userService.findOne(userId);

        ChatUser chatUser = ChatUser.createChatUser(user);

        chat.addChatUser(chatUser);
    }

    // 채팅 나가기
    public void exitChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findOne(chatId);

        chat.removeChatUser(userId);
    }

    // 채팅 삭제
    public void deleteChat(Long chatId) {
        Chat chat = chatRepository.findOne(chatId);
        chatRepository.delete(chat);
    }

}
