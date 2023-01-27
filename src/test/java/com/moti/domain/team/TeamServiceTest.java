package com.moti.domain.team;

import com.moti.domain.exception.CreateTeamNotAllowedException;
import com.moti.domain.exception.NotFoundUserException;
import com.moti.domain.team.entity.Team;
import com.moti.domain.team.entity.TeamUser;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TeamServiceTest {

    @Autowired TeamService teamService;
    @Autowired TeamRepository teamRepository;
    @Autowired EntityManager em;

    User user;
    Team team;

    @BeforeEach
    void init() {
        user = User.builder()
                .email("user@moti.com")
                .password("abcdef")
                .name("user")
                .job(Job.PM)
                .build();

        em.persist(user);

        team = teamService.createTeam(user.getId(), "team");
    }

    @Nested
    @DisplayName("팀 생성")
    class CreateTeam {
        @Test
        @DisplayName("팀 생성 성공")
        public void createTeam() throws Exception {
            // given
            User user = User.builder()
                    .email("jaeyeong@moti.com")
                    .password("abcdef")
                    .name("jaeyeong")
                    .job(Job.PM)
                    .build();

            em.persist(user);

            // when
            Team team = teamService.createTeam(user.getId(), "team1");

            // then
            Team findTeam = teamRepository.findTeamByTeam(team.getId());
            assertThat(findTeam.getName()).isEqualTo("team1");
            assertThat(findTeam.getTeamUsers().size()).isEqualTo(1);
            assertThat(findTeam.getTeamUsers().get(0).getUser().getName()).isEqualTo("jaeyeong");
        }

        @Test
        @DisplayName("DEV가 팀 생성시 실패")
        public void createTeam_failed() throws Exception {
            // given
            User user = User.builder()
                    .email("jaeyeong@moti.com")
                    .password("abcdef")
                    .name("jaeyeong")
                    .job(Job.DEV)
                    .build();

            em.persist(user);

            // when
            CreateTeamNotAllowedException e = assertThrows(CreateTeamNotAllowedException.class, () -> teamService.createTeam(user.getId(), "team1"));

            assertThat(e.getMessage()).isEqualTo("PM만 팀을 만들 수 있습니다.");
        }
    }

    @Nested
    @DisplayName("팀 가입")
    class JoinTeam {
        @Test
        @DisplayName("팀 가입 성공")
        public void joinTeam() throws Exception {
            // given
            User user = User.builder()
                    .email("jaeyeong@moti.com")
                    .password("abcdef")
                    .name("jaeyeong")
                    .job(Job.DEV)
                    .build();

            em.persist(user);

            // when
            teamService.joinTeam(user.getId(), team.getId());

            // then
            Team findTeam = teamRepository.findTeamByTeam(team.getId());
            assertThat(findTeam.getTeamUsers().size()).isEqualTo(2);

            Long count = em.createQuery("select count(tu) from TeamUser tu", Long.class)
                    .getSingleResult();
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("이미 있는 유저일 시 팀 가입 실패")
        public void joinTeam_failed() throws Exception {
            // given
            User user = User.builder()
                    .email("jaeyeong@moti.com")
                    .password("abcdef")
                    .name("jaeyeong")
                    .job(Job.DEV)
                    .build();

            em.persist(user);

            // when
            teamService.joinTeam(user.getId(), team.getId());
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> teamService.joinTeam(user.getId(), team.getId()));

            // then
            assertThat(e.getMessage()).isEqualTo("이미 포함된 유저입니다.");

            Team findTeam = teamRepository.findTeamByTeam(team.getId());
            assertThat(findTeam.getTeamUsers().size()).isEqualTo(2);
        }

    }

    @Nested
    @DisplayName("팀 조회")
    class FindTeam {
        @Test
        @DisplayName("특정 팀의 팀 조회")
        public void findByTeam() throws Exception {
            // when
            Team findTeam = teamService.findTeamByTeam(team.getId());

            // then
            assertThat(findTeam.getId()).isEqualTo(team.getId());
            assertThat(findTeam.getTeamUsers().size()).isEqualTo(1);
            assertThat(findTeam.getTeamUsers().get(0).getUser()).isEqualTo(user);
        }

        @Test
        @DisplayName("모든 팀 조회")
        public void findAllTeam() throws Exception {
            // given
            User newUser1 = User.builder()
                    .email("jaeyeong1@moti.com")
                    .password("abcdef")
                    .name("jaeyeong1")
                    .job(Job.PM)
                    .build();

            User newUser2 = User.builder()
                    .email("jaeyeong2@moti.com")
                    .password("abcdef")
                    .name("jaeyeong2")
                    .job(Job.PM)
                    .build();

            em.persist(newUser1);
            em.persist(newUser2);

            Team team1 = Team.createTeam("팀이름1", newUser1);
            Team team2 = Team.createTeam("팀이름2", newUser1);
            Team team3 = Team.createTeam("팀이름3", newUser1);
            Team team4 = Team.createTeam("팀이름4", newUser1);
            Team team5 = Team.createTeam("팀이름5", newUser2);
            Team team6 = Team.createTeam("팀이름6", newUser2);
            Team team7 = Team.createTeam("팀이름7", newUser2);

            em.persist(team1);
            em.persist(team2);
            em.persist(team3);
            em.persist(team4);
            em.persist(team5);
            em.persist(team6);
            em.persist(team7);

            // when
            List<Team> teams = teamService.findTeams(2, 4);

            // then
            assertThat(teams.size()).isEqualTo(4);
            assertThat(teams.get(0).getName()).isEqualTo("팀이름2");
        }
    }

    @Nested
    @DisplayName("팀 탈퇴")
    class LeaveTeam {
        @Test
        @DisplayName("팀 탈퇴 성공")
        public void leaveTeam() throws Exception {
            // when
            teamService.leaveTeam(user.getId(), team.getId());

            // then
            assertThat(team.getTeamUsers().size()).isEqualTo(0);
            Long count = em.createQuery("select count(tu) from TeamUser tu", Long.class)
                    .getSingleResult();

            assertThat(count).isEqualTo(0L);
        }

        @Test
        @DisplayName("팀 탈퇴 실패")
        public void leaveTeam_failed() throws Exception {
            // when
            NotFoundUserException e = assertThrows(NotFoundUserException.class, () -> teamService.leaveTeam(user.getId()+1, team.getId()));

            // then
            assertThat(e.getMessage()).isEqualTo("존재하지 않는 유저입니다.");
        }
    }

    @Test
    @DisplayName("팀 상태 변경")
    public void changeTeamStatus() throws Exception {
        // when
        teamService.changeTeamStatus(team.getId(), TeamStatus.COMP);

        // then
        assertThat(team.getStatus()).isEqualTo(TeamStatus.COMP);
    }

}