package com.moti.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Chat {

    @Id @GeneratedValue
    @Column(name = "chat_id")
    private Long id;

    @OneToMany(mappedBy = "chat")
    private List<UserChat> userChats = new ArrayList<>();

    @OneToMany(mappedBy = "chat")
    private List<Message> messages = new ArrayList<>();
}
