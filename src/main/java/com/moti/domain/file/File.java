package com.moti.domain.file;

import com.moti.domain.BaseEntity;
import com.moti.domain.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String uploadFileName;

    private String storeFileName;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void setPost(Post post) {
        this.post = post;
    }

    @Builder
    public File(String uploadFileName, String storeFileName, String location) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.location = location;
    }
}
