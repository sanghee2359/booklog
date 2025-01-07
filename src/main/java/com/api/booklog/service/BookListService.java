package com.api.booklog.service;

import com.api.booklog.domain.Book;
import com.api.booklog.domain.BookStatus;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.*;
import com.api.booklog.repository.BookRepository;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.book.BookCreate;
import com.api.booklog.request.book.BookStatusEdit;
import com.api.booklog.request.book.BooksOfYear;
import com.api.booklog.response.BookResponse;
import com.api.booklog.response.PagingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookListService {
    private final UsersRepository userRepository;
    private final BookRepository bookRepository;

    public void saveBook(Long userId, BookCreate bookCreate) {
        // postCreate 라는 클래스를 entity 형태로 변환

        if(bookCreate.getTitle() == null) {
            throw new InvalidRequest("title", "제목을 입력해주세요.");
        }
        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        Book book = bookCreate.toEntity(user);
        validateDates(book);

        bookRepository.save(book);
    }

    // date를 받으면 BookStatus 자동 업데이트
    @Transactional
    public void updateBookStatus(BookStatusEdit request) {
        Book book = bookRepository.findById(request.bookId)
                .orElseThrow(BookNotFound::new);
        handleStatusUpdate(book, request);
    }
    // 상태 변경 로직
    private void handleStatusUpdate(Book book, BookStatusEdit request) {
        switch (book.getStatus()) {
            case NOT_STARTED:
                updateToReading(book, request.getStartDate());
                break;
            case READING:
                updateToCompleted(book, request.getEndDate());
                break;
            default:
                throw new InvalidBookStatus();

        }
    }

    private void updateToReading(Book book, LocalDate startDate) {
        if(startDate == null) throw new InvalidRequest();
        book.startReading(startDate);
    }
    private void updateToCompleted(Book book, LocalDate endDate) {
        if(endDate == null) throw new InvalidRequest();
        book.completeReading(endDate);
    }
    // 책 데이터 삭제
    @Transactional
    public void deleteBook(Long userId, Long bookId) {
        userRepository.findById(userId).orElseThrow(UserNotFound::new);
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFound::new);
        if(!book.getUser().getId().equals(userId)) throw new Unauthorized();
        // 올해의 책이면 삭제 불가 예외 처리
        if (book.isYearBook()) {
            throw new IllegalStateException("올해의 책으로 선정된 책은 삭제할 수 없습니다.");
        }
        bookRepository.deleteById(bookId);
    }

    // 읽을 책 리스트 출력
    public PagingResponse<BookResponse> getPendingBooks(Long userId,int page, int size) {
        Users user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        List<BookStatus> status = List.of(BookStatus.NOT_STARTED, BookStatus.READING);

        // pageable 객체 생성하기 (작성 시기에 따라 sort)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Book> list = bookRepository.findByStatusInAndUser(status, user, pageable);
        return new PagingResponse<>(list, BookResponse.class);
    }
    // 완독한 리스트 출력
    public PagingResponse<BookResponse> getCompletedBooks(Long userId, int page, int size) {
        Users user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        // pageable 객체 생성할 것 (완독 날짜에 따라 sort)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("endDate").descending());
        Page<Book> list = bookRepository.findByStatusAndUser(BookStatus.COMPLETED, user, pageable);
        return new PagingResponse<>(list, BookResponse.class);
    }
    // 올해의 책 리스트 출력 (년도에 따라 다르게 출력)
    public List<BookResponse> getThisYearBooks(Long userId, int year) {
        userRepository.findById(userId).orElseThrow(UserNotFound::new);
        List<Book> books = bookRepository.findByIsYearBookAndUserId(true, userId);
        return books.stream()
                .filter(book -> book.getEndDate() != null && book.getEndDate().getYear() == year)
                .map(BookResponse::new)
                .collect(Collectors.toList());

    }
    // 상태에 맞는 날짜 필드 유효성 검사
    public void validateDates(Book book) {
        if (book.getStatus() == BookStatus.READING && book.getStartDate() == null) {
            throw new IllegalArgumentException("읽기 시작일이 필요합니다.");
        }
    }

    @Transactional
    public void markAsYearBook(Long userId, BooksOfYear request) {
        // 올해의 책이 이미 10개 이상인지 체크
        long yearBookCount = bookRepository.countByIsYearBookAndUserId(true, userId);
        if (yearBookCount >= 10) {
            throw new IllegalStateException("올해의 책은 최대 10개까지만 선정할 수 있습니다.");
        }
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(BookNotFound::new);

        if (book.getStatus() != BookStatus.COMPLETED) {
            throw new IllegalStateException("완독한 책만 올해의 책으로 등록할 수 있습니다.");
        }
        // 명시적 메서드 호출하여 "올해의 책"으로 마킹
        book.markAsYearBook(request.getReview());
        bookRepository.save(book);
    }
}
