package com.moti.web.chat.dto;

import com.moti.domain.message.Message;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseChatWithMessageDto {

  ResponseChatDto responseChatDto;
  List<Message> messages;

  @Builder
  public ResponseChatWithMessageDto(ResponseChatDto responseChatDto, List<Message> messages) {
    this.responseChatDto = responseChatDto;
    this.messages = messages;
  }
}
