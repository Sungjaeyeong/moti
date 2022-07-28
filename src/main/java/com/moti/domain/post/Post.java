package com.moti.domain.post;

import com.moti.domain.BaseEntity;
import com.moti.domain.comment.Comment;
import com.moti.domain.file.File;
import com.moti.domain.post.dto.EditPostServiceDto;
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
public class Post extends BaseEntity {

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    // 연관관계 메서드
    public void setUser(User user) {
        this.user = user;
        user.getPosts().add(this);
    }

    public void setFiles(List<File> files) {
        this.files = files;
        for (File file : files) {
            file.setPost(this);
        }
    }

    @Builder
    public Post(String title, String content, User user, List<File> files) {
        this.title = title;
        this.content = content;
        setUser(user);
        if (files != null) {
            setFiles(files);
        }
    }

    // 포스트 수정
    public void changePost(EditPostServiceDto editPostServiceDto) {
        this.getFiles().clear();

        this.title = editPostServiceDto.getTitle();
        this.content = editPostServiceDto.getContent();
        List<File> files = editPostServiceDto.getFileList();
        if (files != null) {
            this.getFiles().addAll(files);
            for (File file : files) {
                file.setPost(this);
            }
        }
    }

}
