package com.api.booklog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="books")
@RequiredArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String author;
    @ManyToOne
    @JoinColumn
    private Users user;
    @NotNull
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Builder
    public Book(String title, String author, Users user, BookStatus status, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.author = author;
        this.user = user;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
    }

    // 객체의 상태 전환
    public void startReading(LocalDate startDate) {
        if(this.status != BookStatus.NOT_STARTED) {
            throw new IllegalArgumentException("NOT_STARTED 상태에서만 start reading로 변환할 수 있습니다.");
        }
        this.status = BookStatus.READING;
        this.startDate = startDate;
    }
    public void completeReading(LocalDate endDate) {
        if(this.status != BookStatus.READING) {
            throw new IllegalArgumentException("READING 상태에서만 COMPLETE로 변환할 수 있습니다.");
        }
        this.status = BookStatus.COMPLETED;
        this.endDate = endDate;
    }
}
