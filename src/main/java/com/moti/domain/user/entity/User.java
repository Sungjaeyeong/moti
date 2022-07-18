package com.moti.domain.user.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Job job;

    @Lob
    private String introduce;

    @OneToMany(mappedBy = "user")
    private List<UserTeam> userTeams = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserChat> userChats = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, Job job, String introduce, List<UserTeam> userTeams, List<UserChat> userChats, List<Post> posts) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.job = job;
        this.introduce = introduce;
        this.userTeams = userTeams;
        this.userChats = userChats;
        this.posts = posts;
    }
}
