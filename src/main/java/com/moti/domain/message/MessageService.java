package com.moti.domain.message;

import com.moti.domain.chat.ChatRepository;
import com.moti.domain.chat.entity.Chat;
import com.moti.domain.exception.NotMemberInChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;

  // 메세지 보내기(저장)
  public Message createMessage(String messageStr, Long userId, Long chatId) {
    Chat chat = chatRepository.findOne(chatId);
    validateUserInChat(userId, chat);

    Message message = Message.builder()
            .message(messageStr)
            .userId(userId)
            .chat(chat)
            .build();

    messageRepository.save(message);

    return message;
  }

  private void validateUserInChat(Long userId, Chat chat) {
    List<Long> userIds = chat.getChatUsers().stream()
            .map(chatUser -> chatUser.getUser().getId())
            .collect(Collectors.toList());

    if (!userIds.contains(userId)) {
      throw new NotMemberInChatException("유저가 해당 채팅방에 속해 있지 않습니다.");
    }
  }

  // 시스템 안내 메세지
  public Message createSystemMessage(String messageStr, Chat chat) {
    Message systemMessage = Message.builder()
            .message(messageStr)
            .chat(chat)
            .build();

    messageRepository.save(systemMessage);

    return systemMessage;
  }

  // 메세지 삭제
  public void deleteMessage(Long messageId, Long userId) {
    Message message = messageRepository.findOne(messageId);
    message.deleteMessage(userId);

    messageRepository.delete(message);
  }

  // 채팅의 메세지 조회
  @Transactional(readOnly = true)
  public List<Message> findMessagesByChat(Long chatId) {
    Chat chat = chatRepository.findOne(chatId);
    return messageRepository.findMessagesByChat(chat);
  }
}
