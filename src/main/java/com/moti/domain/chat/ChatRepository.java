package com.moti.domain.chat;

import com.moti.domain.chat.entity.Chat;
import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    public Chat findWithUser(Long chatId) {
        try {
            return em.createQuery("select distinct c from Chat c" +
                            " join fetch c.chatUsers cu" +
                            " join fetch cu.user u" +
                            " where c.id = :chatId", Chat.class)
                    .setParameter("chatId", chatId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Chat> findAll() {
        return em.createQuery("select c from Chat c", Chat.class)
                .getResultList();
    }

    public List<Chat> findChatsByUser(Long userId) {
        return em.createQuery("select distinct c from Chat c" +
                        " join fetch c.chatUsers cu" +
                        " where cu.user.id = :userId", Chat.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Chat findByUsers(List<User> users) {
        try {

            return em.createQuery("select c from Chat c" +
                    " join c.chatUsers cu" +
                    " where cu.user = :user1" +
                    " and c in (select c from Chat c" +
                    " join c.chatUsers cu" +
                    " where cu.user = :user2)", Chat.class)
                    .setParameter("user1", users.get(0))
                    .setParameter("user2", users.get(1))
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void delete(Chat chat) {
        em.remove(chat);
    }

    public Long count() {
        return em.createQuery("select count(c) from Chat c", Long.class)
                .getSingleResult();
    }
}
