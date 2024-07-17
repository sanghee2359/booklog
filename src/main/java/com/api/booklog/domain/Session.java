package com.api.booklog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessToken;
    @ManyToOne
    private Users user;

    @Builder
    public Session(Users user) { // accessToken은 새로 생성되므로 파라미터로 받지 않는다
        this.accessToken = UUID.randomUUID().toString();
        this.user = user;
    }
}
