package com.moti.domain.message;

import com.moti.domain.BaseEntity;
import com.moti.domain.chat.Chat;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Message extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    // 연관관계 메서드
    public void setChat(Chat chat) {
        this.chat = chat;
        chat.getMessages().add(this);
    }
}
