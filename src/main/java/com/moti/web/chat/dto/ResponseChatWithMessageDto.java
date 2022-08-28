package com.moti.web.chat.dto;

import com.moti.domain.message.Message;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseChatWithMessageDto {

  ResponseChatDto responseChatDto;
  List<MessageDto> messages;

  @Builder
  public ResponseChatWithMessageDto(ResponseChatDto responseChatDto, List<Message> messages) {
    this.responseChatDto = responseChatDto;
    this.messages = messages.stream().map(
                    message -> new MessageDto(message.getMessage(), message.getUserId(), message.getId())
            )
            .collect(Collectors.toList());
  }

  @Data
  static class MessageDto {

    private Long messageId;
    private Long userId;
    private String message;

    public MessageDto(String message, Long userId, Long messageId) {
      this.message = message;
      this.userId = userId;
      this.messageId = messageId;
    }
  }

}
