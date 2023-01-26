package com.moti.domain.message;

import com.moti.domain.chat.entity.Chat;
import com.moti.domain.chat.entity.ChatUser;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MessageRepositoryTest {

  @Autowired EntityManager em;
  @Autowired MessageRepository messageRepository;

  @Test
  @DisplayName("메세지 저장")
  void save() {
    // given
    User user1 = User.builder()
            .email("user1@moti.com")
            .password("abcdef")
            .name("user1")
            .job(Job.DEV)
            .build();

    User user2 = User.builder()
            .email("user2@moti.com")
            .password("abcdef")
            .name("user2")
            .job(Job.DEV)
            .build();

    em.persist(user1);
    em.persist(user2);

    List<User> userList = Arrays.asList(user1, user2);

    Chat chat = Chat.createChat(userList);
    em.persist(chat);

    Message message = Message.builder()
            .message("hello")
            .userId(user1.getId())
            .chat(chat)
            .build();

    // when
    Long messageId = messageRepository.save(message);

    // then
    Long messageCount = em.createQuery("select count(m) from Message m", Long.class)
            .getSingleResult();

    assertThat(messageCount).isEqualTo(1L);
    assertThat(messageId).isEqualTo(message.getId());

  }

  @Test
  @DisplayName("채팅의 메세지 조회")
  void findMessagesByChat() {
    // given
    User user1 = User.builder()
            .email("user1@moti.com")
            .password("abcdef")
            .name("user1")
            .job(Job.DEV)
            .build();

    User user2 = User.builder()
            .email("user2@moti.com")
            .password("abcdef")
            .name("user2")
            .job(Job.DEV)
            .build();

    em.persist(user1);
    em.persist(user2);

    List<User> userList = Arrays.asList(user1, user2);

    Chat chat = Chat.createChat(userList);
    em.persist(chat);

    Message message1 = Message.builder()
            .message("hello")
            .userId(user1.getId())
            .chat(chat)
            .build();

    Message message2 = Message.builder()
            .message("hi")
            .userId(user2.getId())
            .chat(chat)
            .build();

    Message message3 = Message.builder()
            .message("bye")
            .userId(user1.getId())
            .chat(chat)
            .build();

    em.persist(message1);
    em.persist(message2);
    em.persist(message3);

    // when
    List<Message> messages = messageRepository.findMessagesByChat(chat);

    // then
    assertThat(messages.size()).isEqualTo(3);
    assertThat(messages.get(0).getMessage()).isEqualTo("hello");
    assertThat(messages.get(0).getUserId()).isEqualTo(user1.getId());

  }
}