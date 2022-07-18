package com.moti.domain.file;

import com.moti.domain.BaseEntity;
import com.moti.domain.post.Post;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class File extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String name;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 연관관계 메서드
    public void setPost(Post post) {
        this.post = post;
        post.getFiles().add(this);
    }
}
