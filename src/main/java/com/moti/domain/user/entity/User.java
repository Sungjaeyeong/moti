package com.moti.domain.user.entity;

import com.moti.domain.BaseEntity;
import com.moti.domain.chat.entity.ChatUser;
import com.moti.domain.comment.Comment;
import com.moti.domain.post.Post;
import com.moti.domain.team.entity.TeamUser;
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
    private List<TeamUser> teamUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ChatUser> chatUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, Job job, String introduce) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.job = job;
        this.introduce = introduce;
    }

    // 유저정보 변경
    public void changeInfo(String name, String introduce) {
        this.name = name;
        this.introduce = introduce;
    }
}
