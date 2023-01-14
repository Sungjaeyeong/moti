package com.moti;

import com.moti.domain.chat.entity.Chat;
import com.moti.domain.comment.Comment;
import com.moti.domain.post.Post;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit() {
            User user = User.builder()
                    .email("wodud@afd.com")
                    .password("abcdef")
                    .name("wodud")
                    .job(Job.DEV)
                    .build();

            User user2 = User.builder()
                    .email("wodud2@afd.com")
                    .password("abcdef")
                    .name("wodud2")
                    .job(Job.DEV)
                    .build();

            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .user(user)
                    .build();

            em.persist(user);
            em.persist(user2);
            em.persist(post);

            for (int i=0; i<30; i++) {
                Comment comment = Comment.builder()
                        .content("댓글" + i)
                        .post(post)
                        .user(user)
                        .build();
                em.persist(comment);
            }

        }
    }
}
