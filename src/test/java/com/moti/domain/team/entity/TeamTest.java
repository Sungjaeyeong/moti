package com.moti.domain.team.entity;

import com.moti.domain.team.TeamStatus;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TeamTest {

    @Test
    void createTeam() {
        // given
        User user = User.builder()
                .email("user@moti.com")
                .password("abcdef")
                .name("user")
                .job(Job.PM)
                .build();

        String teamName = "제 1팀";

        // when
        Team team = Team.createTeam(teamName, user);

        // then
        assertThat(team.getName()).isEqualTo(teamName);
        assertThat(team.getStatus()).isEqualTo(TeamStatus.READY);
        assertThat(team.getTeamUsers().size()).isEqualTo(1);
        assertThat(team.getChat()).isNotNull();
    }

}