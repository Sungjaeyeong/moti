package com.moti;

import com.moti.domain.chat.entity.Chat;
import com.moti.domain.comment.Comment;
import com.moti.domain.message.Message;
import com.moti.domain.message.MessageService;
import com.moti.domain.post.Post;
import com.moti.domain.team.TeamService;
import com.moti.domain.team.TeamStatus;
import com.moti.domain.team.entity.Team;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.Arrays;

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
        private final TeamService teamService;
        private final MessageService messageService;

        User user1, user2, user3;
        Team team1, team2;
        Chat chat1, chat2;
        Post post1;
        Comment comment1, comment2;

        public void dbInit() {
            createUser();
            createTeam();
            createChat();
            createMessage();
            createPost();
            createComment();
        }

        private void createUser() {
            user1 = User.builder()
                    .email("user1@moti.com")
                    .password("abcdef")
                    .name("유저1")
                    .job(Job.DEV)
                    .build();

            user2 = User.builder()
                    .email("user2@moti.com")
                    .password("abcdef")
                    .name("유저2")
                    .job(Job.DEV)
                    .build();

            user3 = User.builder()
                    .email("user3@moti.com")
                    .password("abcdef")
                    .name("유저3")
                    .job(Job.PM)
                    .build();

            em.persist(user1);
            em.persist(user2);
            em.persist(user3);
        }

        private void createTeam() {
            team1 = teamService.createTeam(user3.getId(), "1팀");
            teamService.joinTeam(user1.getId(), team1.getId());

            team2 = teamService.createTeam(user3.getId(), "2팀");
            teamService.joinTeam(user2.getId(), team2.getId());
        }

        private void createChat() {
            chat1 = Chat.createChat(Arrays.asList(user1, user3));
            chat2 = Chat.createChat(Arrays.asList(user2, user3));

            em.persist(chat1);
            em.persist(chat2);
        }

        private void createMessage() {
            messageService.createMessage("안녕, 나는 유저1", user1.getId(), chat1.getId());
            messageService.createMessage("안녕, 나는 유저3", user3.getId(), chat1.getId());

            messageService.createMessage("안녕, 나는 유저2", user2.getId(), chat2.getId());
            messageService.createMessage("안녕, 나는 유저3", user3.getId(), chat2.getId());
        }

        private void createPost() {
            post1 = Post.builder()
                    .title("스터디 팀원 모집중")
                    .content("같이 사이드 프로젝트 만들어요.")
                    .user(user3)
                    .build();

            em.persist(post1);
        }

        private void createComment() {
            comment1 = Comment.builder()
                    .post(post1)
                    .user(user1)
                    .content("저 관심 있어요.")
                    .build();

            comment2 = Comment.builder()
                    .post(post1)
                    .user(user2)
                    .content("저도 참여할게요.")
                    .build();

            em.persist(comment1);
            em.persist(comment2);
        }
    }
}
