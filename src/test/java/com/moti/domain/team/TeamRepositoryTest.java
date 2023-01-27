package com.moti.domain.team;

import com.moti.domain.team.entity.Team;
import com.moti.domain.team.entity.TeamUser;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired EntityManager em;
    @Autowired TeamRepository teamRepository;

    Team team;
    User user;

    @BeforeEach
     void init() {
        user = User.builder()
                .email("wodud@afd.com")
                .password("abcdef")
                .name("wodud")
                .job(Job.PM)
                .build();

        team = Team.createTeam("팀이름", user);

        em.persist(user);
        em.persist(team);
    }

    @Test
    @DisplayName("팀 저장")
    public void save() throws Exception {
        // given
        User user = User.builder()
                .email("wodud2@afd.com")
                .password("abcdef")
                .name("wodud")
                .job(Job.PM)
                .build();

        em.persist(user);

        Team team = Team.createTeam("1팀", user);

        // when
        teamRepository.save(team);

        // then
        Long count = em.createQuery("select count(tu) from TeamUser tu", Long.class)
                .getSingleResult();

        assertThat(count).isEqualTo(2L);
        assertThat(teamRepository.count()).isEqualTo(2L);
        assertThat(team.getTeamUsers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("팀 조회")
    public void findOne() throws Exception {
        // when
        Team findTeam = teamRepository.findOne(team.getId());

        // then
        assertThat(findTeam.getName()).isEqualTo("팀이름");
        assertThat(findTeam.getStatus()).isEqualTo(TeamStatus.READY);
    }

    @Test
    @DisplayName("팀 전부 조회")
    public void findAll() throws Exception {
        // given
        Team team1 = Team.createTeam("팀이름1", user);
        Team team2 = Team.createTeam("팀이름2", user);
        Team team3 = Team.createTeam("팀이름3", user);
        Team team4 = Team.createTeam("팀이름4", user);
        Team team5 = Team.createTeam("팀이름5", user);
        Team team6 = Team.createTeam("팀이름6", user);
        Team team7 = Team.createTeam("팀이름7", user);

        em.persist(team1);
        em.persist(team2);
        em.persist(team3);
        em.persist(team4);
        em.persist(team5);
        em.persist(team6);
        em.persist(team7);

        // when
        List<Team> teams1 = teamRepository.findAll(1, 5);
        List<Team> teams2 = teamRepository.findAll(10, 5);

        // then
        assertThat(teams1.size()).isEqualTo(5);
        assertThat(teams1.get(0).getName()).isEqualTo("팀이름1");
        assertThat(teams2.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("유저와 함께 팀 조회 - 팀 아이디로")
    public void findTeamByTeam() throws Exception {
        // given
        User user1 = User.builder()
                .email("wodud2@afd.com")
                .password("abcdef")
                .name("wodud2")
                .job(Job.PM)
                .build();

        em.persist(user1);

        Team team1 = Team.createTeam("1팀", user1);

        em.persist(team1);

        // when
        Team findTeam = teamRepository.findTeamByTeam(team1.getId());

        // then
        assertThat(findTeam.getName()).isEqualTo("1팀");
        assertThat(findTeam.getTeamUsers().size()).isEqualTo(1);
        String userName = findTeam.getTeamUsers().get(0).getUser().getName();
        assertThat(userName).isEqualTo("wodud2");
    }

}