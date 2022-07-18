package com.moti.domain.comment;

import com.moti.domain.BaseEntity;
import com.moti.domain.post.Post;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 연관관계 메서드
    public void setPost(Post post) {
        this.post = post;
        post.getComments().add(this);
    }
}
