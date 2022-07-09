package com.moti.domain.team;

import com.moti.domain.user.entity.UserTeam;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Team {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TeamStatus status;

    @OneToMany(mappedBy = "team")
    private List<UserTeam> userTeams = new ArrayList<>();
}
