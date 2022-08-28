package com.moti.web.message;

import com.moti.domain.message.MessageService;
import com.moti.web.message.dto.DeleteMessageDto;
import com.moti.web.message.dto.SendMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    // 메세지 보내기
    @PostMapping
    public void sendMessage(@RequestBody @Validated SendMessageDto sendMessageDto) {
        messageService.createMessage(sendMessageDto.getMessage(), sendMessageDto.getUserId(), sendMessageDto.getChatId());
    }

    // 메세지 삭제
    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable Long messageId, @RequestBody DeleteMessageDto deleteMessageDto) {
        messageService.deleteMessage(messageId, deleteMessageDto.getUserId());
    }
}
