package com.moti.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Message {

    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;
}
