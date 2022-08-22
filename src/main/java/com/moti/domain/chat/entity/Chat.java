package com.moti.domain.chat.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.message.Message;
import lombok.Builder;
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

    private String name;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUser> chatUsers = new ArrayList<>();

    @OneToMany(mappedBy = "chat")
    private List<Message> messages = new ArrayList<>();

    @Builder
    public Chat(String name) {
        this.name = name;
    }

    // 연관관계 메서드
    public void addChatUser(ChatUser chatUser) {
        this.chatUsers.add(chatUser);
        chatUser.setChat(this);
    }

    // 생성 메서드
    public static Chat createChat(List<ChatUser> chatUserList) {
        StringBuffer str = createChatName(chatUserList);

        Chat chat = Chat.builder()
                .name(String.valueOf(str))
                .build();

        if (chatUserList.size() < 2) {
            throw new IllegalStateException("인원이 부족합니다.");
        }

        for (ChatUser chatUser : chatUserList) {
            chat.addChatUser(chatUser);
        }

        return chat;
    }

    private static StringBuffer createChatName(List<ChatUser> chatUserList) {
        StringBuffer result = new StringBuffer("");
        for (ChatUser chatUser : chatUserList) {
            result.append(chatUser.getUser().getName()).append(" , ");
        }
        return result;
    }
}
