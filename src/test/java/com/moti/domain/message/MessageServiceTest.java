package com.moti.domain.message;

import com.moti.domain.chat.ChatService;
import com.moti.domain.chat.entity.Chat;
import com.moti.domain.exception.NotMemberInChatException;
import com.moti.domain.exception.NotMessageOwnerException;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MessageServiceTest {

  @Autowired MessageService messageService;
  @Autowired ChatService chatService;
  @Autowired EntityManager em;

  Chat initChat;
  User initUser1;
  User initUser2;
  Message initMessage;

  @BeforeEach
  void init() {
    initUser1 = User.builder()
            .email("init_user1@moti.com")
            .password("abcdef")
            .name("init_user1")
            .job(Job.DEV)
            .build();

    initUser2 = User.builder()
            .email("init_user2@moti.com")
            .password("abcdef")
            .name("init_user2")
            .job(Job.DEV)
            .build();

    em.persist(initUser1);
    em.persist(initUser2);

    List<Long> userIds = new ArrayList<>();
    userIds.add(initUser1.getId());
    userIds.add(initUser2.getId());

    initChat = chatService.createChat(userIds);

    em.persist(initChat);

    initMessage = Message.builder()
            .message("init message")
            .userId(initUser1.getId())
            .chat(initChat)
            .build();

    em.persist(initMessage);

  }

  @Test
  @DisplayName("시스템 안내 메세지 생성")
  void createSystemMessage() {
    // when
    Message systemMessage = messageService.createSystemMessage("시스템 메세지입니다.", initChat);

    // then
    Long messageCount = em.createQuery("select count(m) from Message m", Long.class)
            .getSingleResult();

    assertThat(messageCount).isEqualTo(2L);
    assertThat(systemMessage.getUserId()).isNull();
  }

  @Test
  @DisplayName("메세지 보내기")
  void createMessage() {
    // when
    messageService.createMessage("hi", initUser1.getId(), initChat.getId());

    // then
    Long messageCount = em.createQuery("select count(m) from Message m", Long.class)
            .getSingleResult();

    assertThat(messageCount).isEqualTo(2L);
  }

  @Test
  @DisplayName("메세지 보내기 실패 - 본인이 속한 채팅방에만 메세지를 보낼 수 있습니다.")
  void createMessage_failed1() {
    // given
    User user1 = User.builder()
            .email("jaeyeong1@moti.com")
            .password("abcdef")
            .name("jaeyeong1")
            .job(Job.DEV)
            .build();

    em.persist(user1);

    List<Long> userIds = new ArrayList<>();
    userIds.add(user1.getId());
    userIds.add(initUser2.getId());

    Chat chat = chatService.createChat(userIds);

    // when
    NotMemberInChatException e = assertThrows(NotMemberInChatException.class,
            () -> messageService.createMessage("hi", initUser1.getId(), chat.getId()));

    // then
    Long messageCount = em.createQuery("select count(m) from Message m", Long.class)
            .getSingleResult();

    assertThat(messageCount).isEqualTo(1L);
    assertThat(e.getMessage()).isEqualTo("유저가 해당 채팅방에 속해 있지 않습니다.");
  }

  @Test
  @DisplayName("메세지 삭제")
  void deleteMessage() {
    // when
    messageService.deleteMessage(initMessage.getId(), initUser1.getId());

    // then
    Long messageCount = em.createQuery("select count(m) from Message m", Long.class)
            .getSingleResult();

    assertThat(messageCount).isEqualTo(0L);
  }

  @Test
  @DisplayName("메세지 삭제 실패 - 자신이 작성한 메세지만 삭제할 수 있습니다.")
  void deleteMessage_failed1() {
    // when
    NotMessageOwnerException e = assertThrows(NotMessageOwnerException.class,
            () -> messageService.deleteMessage(initMessage.getId(), initUser2.getId()));

    // then
    Long messageCount = em.createQuery("select count(m) from Message m", Long.class)
            .getSingleResult();

    assertThat(messageCount).isEqualTo(1L);
    assertThat(e.getMessage()).isEqualTo("자신이 작성한 메세지만 삭제할 수 있습니다.");
  }

  @Test
  @DisplayName("특정 채팅방 메세지 조회")
  void findMessagesByChat() {
    // given
    messageService.createMessage("hello", initUser2.getId(), initChat.getId());
    messageService.createMessage("abc", initUser1.getId(), initChat.getId());
    messageService.createMessage("def", initUser1.getId(), initChat.getId());

    // when
    List<Message> messages = messageService.findMessagesByChat(initChat.getId());

    // then
    assertThat(messages.size()).isEqualTo(4);
    assertThat(messages.get(3).getMessage()).isEqualTo("def");
  }
}