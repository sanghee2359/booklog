package com.api.booklog.domain;

import com.api.booklog.security.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE user_entity SET is_deleted = true WHERE id = ?")
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    private boolean isDeleted;
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Likes> likes;
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books;

    @Builder
    public UserEntity(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Role 정보를 GrantedAuthority 리스트로 변환
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getAuthority()));
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
    public void deleteUser() {
        this.name = "(삭제된 유저)";
        this.isDeleted = true;
    }

}
