package com.api.booklog.service;

import com.api.booklog.domain.Book;
import com.api.booklog.domain.BookStatus;
import com.api.booklog.domain.UserEntity;
import com.api.booklog.exception.*;
import com.api.booklog.repository.BookRepository;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.book.BookCreate;
import com.api.booklog.request.book.BookEdit;
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

    public void saveBook(String email, BookCreate bookCreate) {
        UserEntity user = findUserByEmail(email);
        if(bookCreate.getTitle() == null) {
            throw new InvalidRequest("title", "제목을 입력해주세요.");
        }
        Book book = bookCreate.toEntity(user);
        validateDates(book);
        bookRepository.save(book);
    }
    
    public BookResponse get (Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow((BookNotFound::new));
        return new BookResponse(book);
    }
    // date를 받으면 BookStatus 자동 업데이트
    @Transactional
    public void updateBookStatus(String email, BookEdit request) {
        UserEntity user = findUserByEmail(email);
        Book book = bookRepository.findById(request.bookId)
                .orElseThrow(BookNotFound::new);
        handleStatusUpdate(user.getId(), book, request);
    }
    // 상태 변경 로직
    private void handleStatusUpdate(Long userId, Book book, BookEdit request) {
        switch (book.getStatus()) {
            case NOT_STARTED:
                updateToReading(book, request.getStartDate());
                break;
            case READING:
                updateToCompleted(userId, book, request);
                break;
            default:
                throw new InvalidBookStatus();

        }
    }

    private void updateToReading(Book book, LocalDate startDate) {
        if(startDate == null) throw new InvalidRequest();
        book.startReading(startDate);
    }
    private void updateToCompleted(Long userId, Book book, BookEdit request) {
        int completedYear = request.endDate.getYear();
        long yearBookCount = bookRepository.countYearBooksByUserAndYear(userId, completedYear);

        if (yearBookCount >= 10) {
            throw new IllegalArgumentException("올해의 책은 매해 최대 10권까지 지정할 수 있습니다.");
        }
        if(request.endDate == null) throw new InvalidRequest();
        book.completeReading(request.endDate, request.review, request.isYearBook);
    }
    // 책 데이터 삭제
    @Transactional
    public void deleteBook(String email, Long bookId) {
        UserEntity user = findUserByEmail(email);
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFound::new);
        if(!book.getUser().getId().equals(user.getId())) throw new Unauthorized();

        bookRepository.deleteById(bookId);
    }

    // 읽을 책 리스트 출력
    public PagingResponse<BookResponse> getPendingBooks(String email, int page, int size) {
        UserEntity user = findUserByEmail(email);
        List<BookStatus> status = List.of(BookStatus.NOT_STARTED, BookStatus.READING);

        // pageable 객체 생성하기 (작성 시기에 따라 sort)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Book> list = bookRepository.findByStatusInAndUser(status, user, pageable);
        return new PagingResponse<>(list, BookResponse.class);
    }
    // 완독한 리스트 출력
    public PagingResponse<BookResponse> getCompletedBooks(String email, int page, int size) {
        UserEntity user = findUserByEmail(email);
        // pageable 객체 생성할 것 (완독 날짜에 따라 sort)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("endDate").descending());
        Page<Book> list = bookRepository.findByStatusAndUser(BookStatus.COMPLETED, user, pageable);
        return new PagingResponse<>(list, BookResponse.class);
    }
    // 올해의 책 리스트 출력 (년도에 따라 다르게 출력)
    public List<BookResponse> getThisYearBooks(String email, int year) {
        UserEntity user = findUserByEmail(email);
        List<Book> books = bookRepository.findYearBooksByUserAndYear(user.getId(), year);
        return books.stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());

    }
    // 특정 유저의 올해의 책 리스트 출력
    public List<BookResponse> getUsersYearBooks(Long userId, int year) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        List<Book> books = bookRepository.findYearBooksByUserAndYear(user.getId(), year);
        return books.stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());

    }
    // 상태에 맞는 날짜 필드 유효성 검사
    public void validateDates(Book book) {
        if (book.getStatus() == BookStatus.READING && book.getStartDate() == null) {
            throw new IllegalArgumentException("읽기 시작일이 필요합니다.");
        }
    }
    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFound::new);
    }
}
