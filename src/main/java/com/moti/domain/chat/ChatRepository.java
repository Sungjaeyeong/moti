package com.moti.domain.chat;

import com.moti.domain.chat.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final EntityManager em;

    public Long save(Chat chat) {
        em.persist(chat);
        return chat.getId();
    }

    public Chat findOne(Long chatId) {
        return em.find(Chat.class, chatId);
    }

    public List<Chat> findAll() {
        return em.createQuery("select c from Chat c", Chat.class)
                .getResultList();
    }

    public void delete(Chat chat) {
        em.remove(chat);
    }

    public Long count() {
        return em.createQuery("select count(c) from Chat c", Long.class)
                .getSingleResult();
    }
}
