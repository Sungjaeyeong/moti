package com.moti.domain.comment;

import com.moti.domain.BaseEntity;
import com.moti.domain.post.Post;
import com.moti.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 연관관계 메서드
    public void setPost(Post post) {
        this.post = post;
        post.getComments().add(this);
    }

    public void setUser(User user) {
        this.user = user;
        user.getComments().add(this);
    }

    @Builder
    public Comment(String content, Post post, User user) {
        this.content = content;
        setPost(post);
        setUser(user);
    }

    public void changeComment(String content) {
        this.content = content;
    }
}
