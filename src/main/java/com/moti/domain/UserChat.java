package com.moti.domain;

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
}
