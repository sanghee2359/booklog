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
    @Lob
    private String content;

    @ManyToOne
    @JoinColumn
    private Users user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<BookMark> bookMarks;
    @Builder
    public Post(String title, String content, Users user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.regDate = LocalDateTime.now();
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
}
