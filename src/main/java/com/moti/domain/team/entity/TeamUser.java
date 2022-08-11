package com.moti.domain.team.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.user.entity.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class TeamUser extends BaseEntity {

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
        user.getTeamUsers().add(this);
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setUserAndTeam(User user, Team team) {
        this.user = user;
        user.getTeamUsers().add(this);
        this.team = team;
        team.getTeamUsers().add(this);
    }

    // 생성 메서드
    public static TeamUser createTeamUser(User user) {
        TeamUser teamUser = new TeamUser();
        teamUser.setUser(user);
        return teamUser;
    }
}
