package com.moti.domain.chat;

import com.moti.domain.chat.entity.Chat;
import com.moti.domain.chat.entity.ChatUser;
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

    // 채팅 생성
    public Chat createChat(List<User> users) {
        List<ChatUser> chatUsers = users.stream().map(user -> ChatUser.createChatUser(user))
                .collect(Collectors.toList());

        Chat chat = Chat.createChat(chatUsers);
        chatRepository.save(chat);

        return chat;
    }

    // 유저가 속한 채팅 조회
    @Transactional(readOnly = true)
    public List<Chat> findChatsByUser(Long userId) {
        return chatRepository.findChatByUser(userId);
    }

    // 채팅 조회
    @Transactional(readOnly = true)
    public Chat findChat(Long chatId) {
        return chatRepository.findWithUser(chatId);
    }

    // 채팅 초대
    public void inviteChat(Long ChatId, Long userId) {

    }

    // 채팅 나가기
    public void exitChat(Long chatId, Long userId) {

    }

    // 채팅 삭제
    public void deleteChat(Long chatId) {
        Chat chat = chatRepository.findOne(chatId);
        chatRepository.delete(chat);
    }

}
