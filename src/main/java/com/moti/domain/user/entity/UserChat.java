package com.moti.domain.user.entity;

import com.moti.domain.chat.Chat;
import com.moti.domain.user.entity.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class UserChat {
    @Id @GeneratedValue
    @Column(name = "user_chat_id")
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
        user.getUserChats().add(this);
    }

    public void setChat(Chat chat) {
        this.chat = chat;
        chat.getUserChats().add(this);
    }

    public void setUserAndChat(User user, Chat chat) {
        this.user = user;
        user.getUserChats().add(this);
        this.chat = chat;
        chat.getUserChats().add(this);
    }
}
