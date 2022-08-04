package com.moti.domain.comment;

import com.moti.domain.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;

    public Long save(Comment comment) {
        em.persist(comment);
        return comment.getId();
    }

    public Comment findOne(Long commentId) {
        return em.find(Comment.class, commentId);
    }

    public List<Comment> findAll() {
        return em.createQuery("select c from Comment c", Comment.class)
                .getResultList();
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }

    // 특정 포스트 댓글 조회
    public List<Comment> findByPost(Post post) {
        return em.createQuery("select c from Comment c" +
                        " where c.post = :post" +
                        " order by c.createdDate desc", Comment.class)
                .setParameter("post", post)
                .getResultList();
    }

    public List<Comment> findByPostWithUser(Post post, int firstIndex, int maxResults) {
        return em.createQuery("select c from Comment c" +
                        " join fetch c.user u" +
                        " where c.post = :post" +
                        " order by c.createdDate desc", Comment.class)
                .setParameter("post", post)
                .setFirstResult(firstIndex)
                .setMaxResults(maxResults)
                .getResultList();
    }
}
