package com.moti.domain.user.entity;

import com.moti.domain.team.Team;
import com.moti.domain.user.entity.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class UserTeam {

    @Id @GeneratedValue
    @Column(name = "user_team_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 연관관계 메서드
    public void setUser(User user) {
        this.user = user;
        user.getUserTeams().add(this);
    }

    public void setTeam(Team team) {
        this.team = team;
        team.getUserTeams().add(this);
    }

    public void setUserAndTeam(User user, Team team) {
        this.user = user;
        user.getUserTeams().add(this);
        this.team = team;
        team.getUserTeams().add(this);
    }
}
