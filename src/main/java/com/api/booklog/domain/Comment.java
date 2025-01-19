package com.api.booklog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
    indexes = {
            @Index(name = "IDX_COMMENT_POST_ID", columnList = "post_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private UserEntity user; // 사용자 정보

    @NotNull
    private String password;

    @NotNull
    private String content;
    @NotNull // 유동닉 필드
    private String author;

    @ManyToOne
    @JoinColumn
    private Post post;
    private LocalDateTime regDate;

    @Builder
    public Comment(UserEntity user, Post post, String password, String content, String author) {
        this.user = user;
        this.post = post;
        this.password = password;
        this.content = content;
        this.author = author; // 유동닉 저장
        this.regDate = LocalDateTime.now();
    }
    public void setPost(Post post) {
        this.post = post;
    }
    public void setUser(UserEntity user) {
        this.user = user;
    }
}
