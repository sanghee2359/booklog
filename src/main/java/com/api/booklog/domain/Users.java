package com.api.booklog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<Session> sessions = new ArrayList<>();
    @Builder
    public Users(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    public Session addSession() {
//        uuid
        Session session = Session.builder()
                .user(this)
                .build();
        sessions.add(session); // session 리스트에 저장
        return session;
    }
}
