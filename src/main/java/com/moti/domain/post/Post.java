package com.moti.domain.post;

import com.moti.domain.user.entity.User;
import com.moti.domain.comment.Comment;
import com.moti.domain.file.File;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Post {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<File> files = new ArrayList<>();

    // 연관관계 메서드
    public void setUser(User user) {
        this.user = user;
        user.getPosts().add(this);
    }
}
