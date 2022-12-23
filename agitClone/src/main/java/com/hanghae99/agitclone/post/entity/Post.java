package com.hanghae99.agitclone.post.entity;

import com.hanghae99.agitclone.comment.entity.Comment;
import com.hanghae99.agitclone.common.TimeStamped;
import com.hanghae99.agitclone.user.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Post extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users user;

    @Column(nullable = false)
    private String content;

    private boolean isModified;

    private long likeCount;

    private long hateCount;

    @OneToMany
    @JoinColumn(name = "postId")
    private List<PostLike> PostLikeList = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "postId")
    private List<Comment> commentList = new ArrayList<>();

}