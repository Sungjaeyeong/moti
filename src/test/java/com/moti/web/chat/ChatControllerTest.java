package com.moti.web.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.chat.ChatRepository;
import com.moti.domain.chat.ChatService;
import com.moti.domain.chat.entity.Chat;
import com.moti.domain.message.MessageService;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.chat.dto.ChangeChatNameDto;
import com.moti.web.chat.dto.CreateChatDto;
import com.moti.web.chat.dto.ExitChatDto;
import com.moti.web.chat.dto.InviteChatDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChatControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;
    @Autowired ChatRepository chatRepository;
    @Autowired ChatService chatService;
    @Autowired MessageService messageService;

    User initUser1;
    User initUser2;
    Chat initChat;
    MockHttpSession session;

    @BeforeEach
    void init() {

        initUser1 = User.builder()
                .email("initUser1@moti.com")
                .password("abcdef")
                .name("initUser1")
                .job(Job.PM)
                .build();

        initUser2 = User.builder()
                .email("initUser2@moti.com")
                .password("abcdef")
                .name("initUser2")
                .job(Job.DEV)
                .build();

        em.persist(initUser1);
        em.persist(initUser2);

        List<Long> userIds = new ArrayList<>();
        userIds.add(initUser1.getId());
        userIds.add(initUser2.getId());

        initChat = chatService.createChat(userIds);

        em.persist(initChat);

        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, initUser1.getId());
    }

    @Nested
    @DisplayName("채팅 생성")
    class CreateChat {
        @Test
        @DisplayName("채팅 생성 성공")
        public void createChat() throws Exception {
            // given
            User user1 = User.builder()
                    .email("User1@moti.com")
                    .password("abcdef")
                    .name("User1")
                    .job(Job.DEV)
                    .build();

            em.persist(user1);

            List<Long> userIds = new ArrayList<>();
            userIds.add(initUser1.getId());
            userIds.add(user1.getId());

            CreateChatDto createChatDto = CreateChatDto.builder()
                    .userIds(userIds)
                    .build();

            String json = objectMapper.writeValueAsString(createChatDto);

            // when
            mockMvc.perform(post("/chats")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .session(session)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            Long count = chatRepository.count();
            assertThat(count).isEqualTo(2L);

            Long chatUserCount = em.createQuery("select count(cu) from ChatUser cu", Long.class)
                    .getSingleResult();
            assertThat(chatUserCount).isEqualTo(4L);
        }

        @Test
        @DisplayName("채팅 생성 실패 - 채팅 생성을 요청하는 유저가 채팅방 유저들에 속해야 한다.")
        public void createChat_failed1() throws Exception {
            User user1 = User.builder()
                    .email("User1@moti.com")
                    .password("abcdef")
                    .name("User1")
                    .job(Job.DEV)
                    .build();

            em.persist(user1);

            List<Long> userIds = new ArrayList<>();
            userIds.add(user1.getId());
            userIds.add(initUser2.getId());

            CreateChatDto createChatDto = CreateChatDto.builder()
                    .userIds(userIds)
                    .build();

            String json = objectMapper.writeValueAsString(createChatDto);

            // when
            mockMvc.perform(post("/chats")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                    .andDo(print());

            // then
            Long count = chatRepository.count();
            assertThat(count).isEqualTo(1L);

            Long chatUserCount = em.createQuery("select count(cu) from ChatUser cu", Long.class)
                    .getSingleResult();
            assertThat(chatUserCount).isEqualTo(2L);
        }
    }

    @Test
    @DisplayName("채팅 조회 메세지 포함")
    public void findUserChat() throws Exception {
        // given
        messageService.createMessage("hello", initUser2.getId(), initChat.getId());
        messageService.createMessage("abc", initUser1.getId(), initChat.getId());
        messageService.createMessage("def", initUser1.getId(), initChat.getId());

        // when
        mockMvc.perform(get("/chats/{chatId}", initChat.getId())
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Nested
    @DisplayName("유저가 속한 채팅 조회")
    class FindUserChat {
        @Test
        @DisplayName("채팅 조회 성공")
        public void findUserChat() throws Exception {
            // when
            mockMvc.perform(get("/chats")
                            .param("userId", String.valueOf(initUser1.getId()))
                            .session(session)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(1))
                    .andExpect(jsonPath("$.chats").exists())
                    .andExpect(jsonPath("$.chats[0].chatId").exists())
                    .andExpect(jsonPath("$.chats[0].chatName").exists())
                    .andExpect(jsonPath("$.chats[0].userCount").exists())
                    .andExpect(jsonPath("$.chats[0].createdAt").exists())
                    .andExpect(jsonPath("$.chats[0].updatedAt").exists())
                    .andDo(print());

        }

        @Test
        @DisplayName("채팅 조회 실패 - 본인의 채팅만 조회할 수 있다.(권한없음)")
        public void findUserChat_failed1() throws Exception {
            // when
            mockMvc.perform(get("/chats")
                            .param("userId", String.valueOf(initUser2.getId()))
                            .session(session)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                    .andDo(print());

        }
    }

    @Test
    @DisplayName("채팅 초대 성공")
    public void inviteChat() throws Exception {
        // given
        User user1 = User.builder()
                .email("User1@moti.com")
                .password("abcdef")
                .name("User1")
                .job(Job.DEV)
                .build();

        em.persist(user1);

        InviteChatDto inviteChatDto = InviteChatDto.builder()
                .chatId(initChat.getId())
                .userId(user1.getId())
                .build();

        String json = objectMapper.writeValueAsString(inviteChatDto);

        // when
        mockMvc.perform(post("/chats/update/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        assertThat(initChat.getChatUsers().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("채팅 나가기 성공")
    public void exitChat() throws Exception {
        // given
        ExitChatDto exitChatDto = ExitChatDto.builder()
                .chatId(initChat.getId())
                .userId(initUser1.getId())
                .build();

        String json = objectMapper.writeValueAsString(exitChatDto);

        // when
        mockMvc.perform(post("/chats/delete/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        assertThat(initChat.getChatUsers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("채팅방 제목 변경 성공")
    public void changeChatName() throws Exception {
        // given
        ChangeChatNameDto changeChatNameDto = ChangeChatNameDto.builder()
                .name("변경 제목")
                .chatId(initChat.getId())
                .build();

        String json = objectMapper.writeValueAsString(changeChatNameDto);

        // when
        mockMvc.perform(post("/chats/update/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        assertThat(initChat.getName()).isEqualTo("변경 제목");
    }
}