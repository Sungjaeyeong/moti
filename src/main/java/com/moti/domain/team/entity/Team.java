package com.moti.domain.team.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.chat.entity.Chat;
import com.moti.domain.exception.CreateTeamNotAllowedException;
import com.moti.domain.exception.NotFoundUserException;
import com.moti.domain.team.TeamStatus;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Team extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TeamStatus status;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TeamUser> teamUsers = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Builder
    public Team(String name, TeamStatus status, Chat chat) {
        this.name = name;
        this.status = status;
        this.chat = chat;
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
    public static Team createTeam(String name, User user) {
        if (user.getJob() != Job.PM) {
            throw new CreateTeamNotAllowedException("PM만 팀을 만들 수 있습니다.");
        }

        TeamUser teamUser = TeamUser.createTeamUser(user);
        Chat chat = Chat.createChat(List.of(user));

        Team team = Team.builder()
                .name(name)
                .status(TeamStatus.READY)
                .chat(chat)
                .build();
        team.addTeamUser(teamUser);
        return team;
    }

    public void changeTeamStatus(TeamStatus teamStatus) {
        this.status = teamStatus;
    }

}
