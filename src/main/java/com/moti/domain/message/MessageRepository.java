package com.moti.domain.message;

import com.moti.domain.chat.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

  private final EntityManager em;

  public Long save(Message message) {
    em.persist(message);
    return message.getId();
  }

  public Message findOne(Long messageId) {
    return em.find(Message.class, messageId);
  }

  public List<Message> findMessagesByChat(Chat chat) {
    return em.createQuery("select m from Message m" +
                    " where m.chat = :chat" +
                    " order by m.createdDate asc", Message.class)
            .setParameter("chat", chat)
            .getResultList();
  }

  public void delete(Message message) {
    em.remove(message);
  }
}
