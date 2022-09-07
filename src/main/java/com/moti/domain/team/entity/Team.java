package com.moti.domain.team.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.exception.CreateTeamNotAllowedException;
import com.moti.domain.exception.NotFoundUserException;
import com.moti.domain.team.TeamStatus;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TeamStatus status;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamUser> teamUsers = new ArrayList<>();

    @Builder
    public Team(String name, TeamStatus status) {
        this.name = name;
        this.status = status;
    }

    // 연관관계 메서드
    public void addTeamUser(TeamUser teamUser) {
        User user = teamUser.getUser();
        this.teamUsers.forEach(tu -> {
            if (user.equals(tu.getUser())) {
                throw new IllegalStateException("이미 포함된 유저입니다.");
            }
        });

        this.teamUsers.add(teamUser);
        teamUser.setTeam(this);
    }

    public void removeTeamUser(User user) {
        TeamUser findTeamUser = this.teamUsers.stream()
                .filter(teamUser -> teamUser.getUser() == user)
                .findFirst()
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 유저입니다."));

        this.teamUsers.remove(findTeamUser);
    }

    // 생성 메서드
    public static Team createTeam(String name, TeamUser teamUser) {

        if (teamUser.getUser().getJob() != Job.PM) {
            throw new CreateTeamNotAllowedException("PM만 팀을 만들 수 있습니다.");
        }
        Team team = Team.builder()
                .name(name)
                .status(TeamStatus.READY)
                .build();
        team.addTeamUser(teamUser);
        return team;
    }

    public void changeTeamStatus(TeamStatus teamStatus) {
        this.status = teamStatus;
    }

}
