package com.moti.domain.chat.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.exception.NotFoundUserException;
import com.moti.domain.message.Message;
import com.moti.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private void addChatUser(ChatUser chatUser) {
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
    public static Chat createChat(List<User> userList) {
        StringBuffer str = createChatName(userList);

        Chat chat = Chat.builder()
                .name(String.valueOf(str))
                .build();

        chat.addUsers(userList);

        return chat;
    }

    public void addUsers(List<User> userList) {
        validateDuplicatedUser(userList);
        addChatUsers(userList);
    }

    private void validateDuplicatedUser(List<User> userList) {
        this.chatUsers.forEach(cu -> {
            if (userList.contains(cu.getUser())) {
                throw new IllegalStateException("이미 포함된 유저입니다.");
            }
        });
    }

    private void addChatUsers(List<User> userList) {
        for (User user : userList) {
            ChatUser chatUser = ChatUser.createChatUser(user);
            addChatUser(chatUser);
        }
    }

    private static StringBuffer createChatName(List<User> userList) {
        StringBuffer result = new StringBuffer("");
        for (User user : userList) {
            result.append(user.getName())
                    .append(", ");
        }
        result.delete(result.length()-2, result.length());

        return result;
    }

    public void changeChatName(String name) {
        this.name = name;
    }
}
