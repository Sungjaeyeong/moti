package com.moti.web.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.chat.ChatService;
import com.moti.domain.chat.entity.Chat;
import com.moti.domain.message.Message;
import com.moti.domain.message.MessageService;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.message.dto.DeleteMessageDto;
import com.moti.web.message.dto.SendMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.moti.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class MessageControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;
    @Autowired ChatService chatService;
    @Autowired MessageService messageService;

    User initUser1;
    User initUser2;
    Chat initChat;
    MockHttpSession session;
    Message initMessage;

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

        initMessage = messageService.createMessage("hello", initUser2.getId(), initChat.getId());

        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, initUser1.getId());
    }

    @Test
    @DisplayName("메세지 보내기")
    public void sendMessage() throws Exception {
        // given
        SendMessageDto sendMessageDto = SendMessageDto.builder()
                .message("hi")
                .chatId(initChat.getId())
                .userId(initUser1.getId())
                .build();

        String json = objectMapper.writeValueAsString(sendMessageDto);

        // when
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("create-message",
                        requestFields(
                                fieldWithPath("message").description("메세지 내용").attributes(Attributes.key("required").value("O")),
                                fieldWithPath("chatId").description("채팅 ID").attributes(Attributes.key("required").value("O")),
                                fieldWithPath("userId").description("유저 ID").attributes(Attributes.key("required").value("O"))
                        )
                ));

        // then
        Long messageCount = em.createQuery("select count(m) from Message m", Long.class)
                .getSingleResult();
        assertThat(messageCount).isEqualTo(2L);
    }

    @Test
    @DisplayName("메세지 삭제")
    public void deleteMessage() throws Exception {
        // given
        DeleteMessageDto deleteMessageDto = DeleteMessageDto.builder()
                .userId(initUser2.getId())
                .build();

        String json = objectMapper.writeValueAsString(deleteMessageDto);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/messages/{messageId}", initMessage.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-message",
                        pathParameters(parameterWithName("messageId").description("메세지 ID").attributes(Attributes.key("required").value("O")))
                ));

        // then
        Long messageCount = em.createQuery("select count(m) from Message m", Long.class)
                .getSingleResult();
        assertThat(messageCount).isEqualTo(0L);
    }
}