package com.moti.domain.chat.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.user.entity.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ChatUser extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "chat_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    // 연관관계 메서드
    public void setUser(User user) {
        this.user = user;
        user.getChatUsers().add(this);
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    // 생성 메서드
    public static ChatUser createChatUser(User user) {
        ChatUser chatUser = new ChatUser();
        chatUser.setUser(user);
        return chatUser;
    }
}
