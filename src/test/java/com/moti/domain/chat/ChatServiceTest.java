package com.moti.domain.chat;

import com.moti.domain.chat.entity.Chat;
import com.moti.domain.exception.DuplicateChatException;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChatServiceTest {

  @Autowired ChatService chatService;
  @Autowired ChatRepository chatRepository;
  @Autowired EntityManager em;

  Chat initChat;
  User initUser1;
  User initUser2;

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

    List<User> users = new ArrayList<>();
    users.add(initUser1);
    users.add(initUser2);

    initChat = chatService.createChat(users);
  }

  @Nested
  @DisplayName("채팅 생성")
  class CreateChat {
    @Test
    @DisplayName("채팅 생성 성공")
    public void createChat() throws Exception {
      // given
      User user1 = User.builder()
              .email("jaeyeong1@moti.com")
              .password("abcdef")
              .name("jaeyeong1")
              .job(Job.DEV)
              .build();

      User user2 = User.builder()
              .email("jaeyeong2@moti.com")
              .password("abcdef")
              .name("jaeyeong2")
              .job(Job.DEV)
              .build();

      em.persist(user1);
      em.persist(user2);

      List<User> users = new ArrayList<>();
      users.add(user1);
      users.add(user2);

      // when
      Chat chat = chatService.createChat(users);

      // then
      List<User> chatUsers = chat.getChatUsers().stream()
              .map(chatUser -> chatUser.getUser())
              .collect(Collectors.toList());

      Assertions.assertThat(chatRepository.count()).isEqualTo(2L);
      assertThat(chatUsers).contains(user1, user2);
    }

    @Test
    @DisplayName("채팅 생성 실패 - 인원 부족")
    public void createChat_failed1() throws Exception {
      // given
      User user1 = User.builder()
              .email("jaeyeong1@moti.com")
              .password("abcdef")
              .name("jaeyeong1")
              .job(Job.DEV)
              .build();

      em.persist(user1);

      List<User> users = new ArrayList<>();
      users.add(user1);

      // when
      IllegalStateException e = assertThrows(IllegalStateException.class, () -> chatService.createChat(users));

      assertThat(e.getMessage()).isEqualTo("인원이 부족합니다.");
    }

    @Test
    @DisplayName("채팅 생성 실패 - 중복 1:1 채팅")
    public void createChat_failed2() throws Exception {
      // given
      User user1 = User.builder()
              .email("jaeyeong1@moti.com")
              .password("abcdef")
              .name("jaeyeong1")
              .job(Job.DEV)
              .build();

      User user2 = User.builder()
              .email("jaeyeong2@moti.com")
              .password("abcdef")
              .name("jaeyeong2")
              .job(Job.DEV)
              .build();

      em.persist(user1);
      em.persist(user2);

      List<User> users = new ArrayList<>();
      users.add(user1);
      users.add(user2);

      // when
      chatService.createChat(users);
      DuplicateChatException e = assertThrows(DuplicateChatException.class, () -> chatService.createChat(users));

      assertThat(e.getMessage()).isEqualTo("이미 채팅방이 존재합니다.");
    }
  }

  @Test
  @DisplayName("유저가 속한 채팅 조회")
  public void findChatsByUser() throws Exception {
    // given
    User user1 = User.builder()
            .email("jaeyeong1@moti.com")
            .password("abcdef")
            .name("jaeyeong1")
            .job(Job.DEV)
            .build();

    User user2 = User.builder()
            .email("jaeyeong2@moti.com")
            .password("abcdef")
            .name("jaeyeong2")
            .job(Job.DEV)
            .build();

    em.persist(user1);
    em.persist(user2);

    List<User> users1 = new ArrayList<>();
    users1.add(initUser1);
    users1.add(user1);

    List<User> users2 = new ArrayList<>();
    users2.add(initUser1);
    users2.add(user2);

    chatService.createChat(users1);
    chatService.createChat(users2);

    // when
    List<Chat> chatList = chatService.findChatsByUser(initUser1.getId());

    // then
    assertThat(chatList.size()).isEqualTo(3);
  }

  @Test
  @DisplayName("채팅 조회")
  public void findChat() throws Exception {
    // when
    Chat chat = chatService.findChat(initChat.getId());

    // then
    assertThat(chat).isEqualTo(initChat);
  }

  @Test
  @DisplayName("채팅방 이름 변경")
  public void changeChatName() throws Exception {
    // when
    chatService.changeChatName(initChat.getId(), "변경된 이름");

    // then
    assertThat(initChat.getName()).isEqualTo("변경된 이름");
  }

  @Test
  @DisplayName("채팅 초대")
  public void inviteChat() throws Exception {
    // given
    User user = User.builder()
            .email("new_user@moti.com")
            .password("abcdef")
            .name("new_user")
            .job(Job.DEV)
            .build();

    em.persist(user);

    // when
    chatService.inviteChat(initChat.getId(), user.getId());

    // then
    List<User> users = initChat.getChatUsers().stream()
            .map(chatUser -> chatUser.getUser())
            .collect(Collectors.toList());

    assertThat(users).contains(user);
  }

  @Test
  @DisplayName("채팅 나가기")
  public void exitChat() throws Exception {
    // when
    chatService.exitChat(initChat.getId(), initUser2.getId());

    // then
    List<User> users = initChat.getChatUsers().stream()
            .map(chatUser -> chatUser.getUser())
            .collect(Collectors.toList());

    assertThat(users).doesNotContain(initUser2);
  }

  @Test
  @DisplayName("채팅 삭제")
  public void deleteChat() throws Exception {
    // when
    chatService.deleteChat(initChat.getId());

    // then
    Assertions.assertThat(chatRepository.count()).isEqualTo(0L);
    Long chatUserCount = em.createQuery("select count(cu) from ChatUser cu", Long.class)
            .getSingleResult();
    assertThat(chatUserCount).isEqualTo(0L);
  }

}