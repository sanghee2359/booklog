package com.api.booklog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Lob
    private String content;
    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
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
}
