package com.api.booklog.service;

import com.api.booklog.domain.Book;
import com.api.booklog.domain.BookStatus;
import com.api.booklog.domain.Post;
import com.api.booklog.domain.Users;
import com.api.booklog.exception.BookNotFound;
import com.api.booklog.exception.InvalidBookStatus;
import com.api.booklog.exception.InvalidRequest;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.BookRepository;
import com.api.booklog.repository.UsersRepository;
import com.api.booklog.request.book.BookCreate;
import com.api.booklog.request.book.BookEdit;
import com.api.booklog.request.post.PostSearch;
import com.api.booklog.response.BookResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
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

    // BookStatus 업데이트
    @Transactional
    public void updateBookStatus(Long bookId, BookEdit request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(BookNotFound::new);
        // 요청으로부터 상태 및 날짜 추출
        BookStatus newStatus = request.getStatus();
        LocalDate date = request.getDate();
        switch (newStatus) {
            case READING:
                book.startReading(date);
                break;
            case COMPLETED:
                book.completeReading(date);
                break;
            default:
                throw new InvalidBookStatus();
        }
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
    // 읽을 책 리스트 출력
    public PagingResponse<BookResponse> getCompletedBooks(Long userId, int page, int size) {
        Users user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        // pageable 객체 생성할 것 (완독 날짜에 따라 sort)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("endDate").descending());
        Page<Book> list = bookRepository.findByStatusAndUser(BookStatus.COMPLETED, user, pageable);
        return new PagingResponse<>(list, BookResponse.class);
    }
    // 상태에 맞는 날짜 필드 유효성 검사
    public void validateDates(Book book) {
        if (book.getStatus() == BookStatus.READING && book.getStartDate() == null) {
            throw new IllegalArgumentException("읽기 시작일이 필요합니다.");
        }
        if (book.getStatus() == BookStatus.COMPLETED && book.getEndDate() == null) {
            throw new IllegalArgumentException("완독일이 필요합니다.");
        }
    }
}
