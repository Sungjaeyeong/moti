package com.moti.domain.message;

import com.moti.domain.chat.ChatRepository;
import com.moti.domain.chat.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;

  // 메세지 보내기(저장)
  public Message createMessage(String messageStr, Long userId, Long chatId) {
    Chat chat = chatRepository.findOne(chatId);

    Message message = Message.builder()
            .message(messageStr)
            .userId(userId)
            .chat(chat)
            .build();

    messageRepository.save(message);

    return message;
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
