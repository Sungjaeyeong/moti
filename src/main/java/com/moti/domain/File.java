package com.moti.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class File {

    @Id @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String name;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

}
