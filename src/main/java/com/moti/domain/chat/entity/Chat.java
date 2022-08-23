package com.moti.domain.chat.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.exception.NotFoundUserException;
import com.moti.domain.message.Message;
import com.moti.domain.user.entity.User;
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
        User user = chatUser.getUser();
        this.chatUsers.forEach(cu -> {
            if (user.equals(cu.getUser())) {
                throw new IllegalStateException("이미 포함된 유저입니다.");
            }
        });

        this.chatUsers.add(chatUser);
        chatUser.setChat(this);
    }

    public void removeChatUser(Long userId) {
        ChatUser findChatUser = this.chatUsers.stream()
                .filter(chatUser -> chatUser.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 유저입니다."));

        this.chatUsers.remove(findChatUser);
    }

    // 생성 메서드
    public static Chat createChat(List<ChatUser> chatUserList) {
        StringBuffer str = createChatName(chatUserList);

        Chat chat = Chat.builder()
                .name(String.valueOf(str))
                .build();

        for (ChatUser chatUser : chatUserList) {
            chat.addChatUser(chatUser);
        }

        return chat;
    }

    private static StringBuffer createChatName(List<ChatUser> chatUserList) {
        StringBuffer result = new StringBuffer("");
        for (ChatUser chatUser : chatUserList) {
            result.append(chatUser.getUser().getName())
                    .append(", ");
        }
        result.delete(result.length()-2, result.length());

        return result;
    }

    public void changeChatName(String name) {
        this.name = name;
    }
}
