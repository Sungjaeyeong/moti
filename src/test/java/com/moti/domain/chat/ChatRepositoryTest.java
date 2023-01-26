package com.moti.domain.chat;

import com.moti.domain.chat.entity.Chat;
import com.moti.domain.chat.entity.ChatUser;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ChatRepositoryTest {

    @Autowired EntityManager em;
    @Autowired ChatRepository chatRepository;

    User user1;
    User user2;
    Chat chat1;

    @BeforeEach
    void init() {
        user1 = User.builder()
                .email("wodud@afd.com")
                .password("abcdef")
                .name("wodud")
                .job(Job.DEV)
                .build();

        user2 = User.builder()
                .email("wodud2@afd.com")
                .password("abcdef")
                .name("wodud2")
                .job(Job.DEV)
                .build();

        em.persist(user1);
        em.persist(user2);

        List<User> userList = Arrays.asList(user1, user2);

//        ChatUser chatUser1 = ChatUser.createChatUser(user1);
//        ChatUser chatUser2 = ChatUser.createChatUser(user2);
//
//        List<ChatUser> chatUserList = new ArrayList<>();
//        chatUserList.add(chatUser1);
//        chatUserList.add(chatUser2);
//
//        chat1 = Chat.createChat(chatUserList);
        chat1 = Chat.createChat(userList);

        em.persist(chat1);
    }

    @Test
    @DisplayName("채팅 저장")
    void save() {
        // given
        User user3 = User.builder()
                .email("wodud3@afd.com")
                .password("abcdef")
                .name("wodud3")
                .job(Job.DEV)
                .build();

        User user4 = User.builder()
                .email("wodud4@afd.com")
                .password("abcdef")
                .name("wodud4")
                .job(Job.DEV)
                .build();

        em.persist(user3);
        em.persist(user4);

        List<User> userList = Arrays.asList(user3, user4);

        Chat chat2 = Chat.createChat(userList);

        // when
        chatRepository.save(chat2);

        // then
        Long chatCount = chatRepository.count();
        assertThat(chatCount).isEqualTo(2L);

        Long chatUserCount = em.createQuery("select count(cu) from ChatUser cu", Long.class)
                .getSingleResult();
        assertThat(chatUserCount).isEqualTo(4L);
    }

    @Test
    @DisplayName("한개 채팅 조회")
    public void findOne() throws Exception {
        // when
        Chat findChat = chatRepository.findOne(chat1.getId());

        // then
        assertThat(findChat).isEqualTo(chat1);
    }

    @Test
    @DisplayName("유저정보와 함께 채팅 조회")
    public void findWithUser() throws Exception {
        // when
        Chat findChat = chatRepository.findWithUser(chat1.getId());

        // then
        assertThat(findChat.getChatUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("모든 채팅 조회")
    public void findAll() throws Exception {
        // given
        User user3 = User.builder()
                .email("wodud3@afd.com")
                .password("abcdef")
                .name("wodud3")
                .job(Job.DEV)
                .build();

        User user4 = User.builder()
                .email("wodud4@afd.com")
                .password("abcdef")
                .name("wodud4")
                .job(Job.DEV)
                .build();

        em.persist(user3);
        em.persist(user4);

        List<User> userList = Arrays.asList(user3, user4);

        Chat chat2 = Chat.createChat(userList);

        em.persist(chat2);

        // when
        List<Chat> chatList = chatRepository.findAll();

        // then
        assertThat(chatList.size()).isEqualTo(2);
        assertThat(chatList.get(0)).isEqualTo(chat1);
    }

    @Test
    @DisplayName("유저가 속한 채팅 조회")
    public void findChatsByUser() throws Exception {
        // given
        User user3 = User.builder()
                .email("wodud3@afd.com")
                .password("abcdef")
                .name("wodud3")
                .job(Job.DEV)
                .build();

        User user4 = User.builder()
                .email("wodud4@afd.com")
                .password("abcdef")
                .name("wodud4")
                .job(Job.DEV)
                .build();

        em.persist(user3);
        em.persist(user4);

        List<User> userList1 = Arrays.asList(user1, user3);
        List<User> userList2 = Arrays.asList(user1, user4);

        Chat chat2 = Chat.createChat(userList1);
        Chat chat3 = Chat.createChat(userList2);

        em.persist(chat2);
        em.persist(chat3);

        // when
        List<Chat> chatList = chatRepository.findChatsByUser(user1.getId());

        // then
        assertThat(chatList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("유저들로 채팅 조회")
    public void findByUsers() throws Exception {
        // given
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        // when
        Chat findChat = chatRepository.findByUsers(users);

        // then
        List<User> userList = findChat.getChatUsers().stream()
                .map(chatUser -> chatUser.getUser())
                .collect(Collectors.toList());

        assertThat(userList).contains(user1, user2);
    }

    @Test
    @DisplayName("채팅 삭제")
    public void delete() throws Exception {
        // when
        chatRepository.delete(chat1);

        // then
        Long chatCount = chatRepository.count();
        assertThat(chatCount).isEqualTo(0L);
    }

}