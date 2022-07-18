package com.moti.domain.chat;

import com.moti.domain.BaseEntity;
import com.moti.domain.message.Message;
import com.moti.domain.user.entity.UserChat;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Chat extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chat_id")
    private Long id;

    @OneToMany(mappedBy = "chat")
    private List<UserChat> userChats = new ArrayList<>();

    @OneToMany(mappedBy = "chat")
    private List<Message> messages = new ArrayList<>();
}
