package com.moti.domain.message;

import com.moti.domain.BaseEntity;
import com.moti.domain.chat.entity.Chat;
import com.moti.domain.exception.NotMemberInChatException;
import com.moti.domain.exception.NotMessageOwnerException;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
public class Message extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @Length(min = 1)
    private String message;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    // 연관관계 메서드
    public void setChat(Chat chat) {
        this.chat = chat;
        chat.getMessages().add(this);
    }

    @Builder
    public Message(String message, Long userId, Chat chat) {
        validateUserInChat(userId, chat);

        this.message = message;
        this.userId = userId;
        this.setChat(chat);
    }

    private void validateUserInChat(Long userId, Chat chat) {
        List<Long> userIds = chat.getChatUsers().stream()
                .map(chatUser -> chatUser.getUser().getId())
                .collect(Collectors.toList());

        if (!userIds.contains(userId)) {
            throw new NotMemberInChatException("유저가 해당 채팅방에 속해 있지 않습니다.");
        }
    }

    public void deleteMessage(Long userId) {
        validateMessageOwner(userId);

        this.chat.getMessages().remove(this);
    }

    private void validateMessageOwner(Long userId) {
        if (!userId.equals(this.getUserId())) {
            throw new NotMessageOwnerException("자신이 작성한 메세지만 삭제할 수 있습니다.");
        }
    }

}
