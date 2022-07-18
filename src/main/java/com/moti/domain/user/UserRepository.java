package com.moti.domain.user;

import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public Long save(User user) {
        em.persist(user);
        return user.getId();
    }

    public User findOne(Long id) {
        return em.find(User.class, id);
    }

    public Optional<User> findByEmail(String email) {
        return em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst();
    }

    public List<User> findAll() {
        return em.createQuery("select u from User u", User.class)
                .getResultList();
    }

    public void delete(User user) {
        em.remove(user);
    }

    public void deleteAll() {
        for (User u: findAll()) {
            delete(u);
        }
    }

    public long count() {
        return em.createQuery("select count(u) from User u", Long.class)
                .getSingleResult();
    }

    /**
     * 특정 조건으로 유저 찾기
     */

}
