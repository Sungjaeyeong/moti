package com.moti.domain.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;

    public Long save(Post post) {
        em.persist(post);
        return post.getId();
    }

    public Post findOne(Long postId) {
        return em.find(Post.class, postId);
    }

    public List<Post> findAll() {
        return em.createQuery("select p from Post p", Post.class)
                .getResultList();
    }

    public void delete(Post post) {
        em.remove(post);
    }

    public Long count() {
        return em.createQuery("select count(p) from Post p", Long.class)
                .getSingleResult();
    }

    // 검색
    public List<Post> findSearch(String searchWord) {
        return em.createQuery("select p from Post p " +
                        "where p.title like :searchWord " +
                        "OR p.content like :searchWord", Post.class)
                .setParameter("searchWord", "%" + searchWord + "%")
                .getResultList();
    }

    // 특정 유저의 포스트 찾기
}
