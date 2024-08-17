package com.api.booklog.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class BookMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name="user_id")
    private Users user;
}
