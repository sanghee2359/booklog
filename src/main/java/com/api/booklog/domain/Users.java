package com.api.booklog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Post> posts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Likes> likes;

    @Builder
    public Users(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }
    public UserEditor.UserEditorBuilder toEditor() {
        return UserEditor.builder()
                .name(name)
                .email(email)
                .password(password);
    }

    public void edit(UserEditor userEditor) { // PostEditor 하나만 인자로 받는 메서드로 개선.
        name = userEditor.getName();
        email = userEditor.getEmail();
        password = userEditor.getPassword();
    }
}
