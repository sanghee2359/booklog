package com.api.booklog.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Column(nullable = false)
    private LocalDateTime regDate;
    @Lob // Large Object를 나타냄 (텍스트 또는 바이너리 대용량 데이터)
    @Column(columnDefinition = "LONGTEXT") // DB column의 데이터 타입을 명시적으로 정의
    private String content;
    @Column(name = "likes_count")
    private Long likesCount = 0L;

    @ManyToOne
    @JoinColumn
    private Users user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<BookMark> bookMarks;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<Likes> likes;
    @Builder
    public Post(String title, String content, Users user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.regDate = LocalDateTime.now();
        this.likesCount = 0L;
    }




    public PostEditor.PostEditorBuilder toEditor() {
         return PostEditor.builder()
                .title(title)
                .content(content);
    }

    public void edit(PostEditor postEditor) { // PostEditor 하나만 인자로 받는 메서드로 개선.
        title = postEditor.getTitle();
        content = postEditor.getContent();
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public void addComment(Comment comment) {
        comment.setPost(this); // comment가 현재 포스트임을 명시
        this.comments.add(comment); // comment list에 add
    }

    // likeCount 증가
    public void incrementLikeCount() {
        this.likesCount++;
    }

    // likeCount 감소 (likeCount가 0보다 클 때만 감소)
    public void decrementLikeCount() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }
}
