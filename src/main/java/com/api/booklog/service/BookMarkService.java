package com.api.booklog.service;

import com.api.booklog.domain.Post;
import com.api.booklog.exception.AlreadyBookmark;
import com.api.booklog.exception.BookmarkNotFound;
import com.api.booklog.exception.PostNotFound;
import com.api.booklog.exception.UserNotFound;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.response.BookmarkResponse;
import com.api.booklog.response.PagingResponse;
import com.api.booklog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private static final String BOOKMARK_KEY_PREFIX = "user:bookmarks:";

    public void addBookmark(Long userId, Long postId) {
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        // 이미 북마크에 존재하는지 확인
        String key = makeKey(userId);
        if(isMemberOfZSet(key, postId)) {
            throw new AlreadyBookmark();
        }

        // current time을 score로 사용하여 추가
        redisTemplate.opsForZSet().add(key, postId, System.currentTimeMillis());
        log.debug("Bookmark added: userId={}, postId={}, key={}", userId, postId, key); // 로그 추가

    }

    public PagingResponse<PostResponse> getBookmarks(Long userId, int page, int size) {
        userRepository.findById(userId).orElseThrow(UserNotFound::new);
        String key = makeKey(userId);
        long totalElements = 0;
        // 전체 북마크 수
        if(redisTemplate.opsForZSet().size(key)!=null) {
            totalElements = redisTemplate.opsForZSet().size(key);
        }
        long start = (long)(page - 1) * size;
        long end = start + size - 1;

        // 특정 범위의 데이터 가져오기 (ZSet에서 범위 가져오기)
        Set<ZSetOperations.TypedTuple<Object>> bookmarkTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);

        // 북마크 ID 리스트 생성
        assert bookmarkTuples != null;
        List<Long> bookmarkIds = bookmarkTuples.stream()
                .map(tuple -> Long.valueOf(Objects.requireNonNull(tuple.getValue()).toString()))
                .toList();


        // 데이터베이스에서 정렬된 포스트 조회
        List<Post> posts =  postRepository.findPostsByIdsOrderByIdCustom(bookmarkIds);

        Page<Post> postPage = new PageImpl<>(posts,
                PageRequest.of(page - 1, size),
                totalElements);


        // PagingResponse 생성
        PagingResponse<PostResponse> pagingResponse = new PagingResponse<>(postPage, PostResponse.class);
        pagingResponse.setHasNextPage(totalElements > (start + size));

        return pagingResponse;
    }

    public void removeBookmark(Long userId, Long postId) {
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        String key = makeKey(userId);
        if(!isMemberOfZSet(key, postId)) {
            throw new BookmarkNotFound();
        }
        redisTemplate.opsForZSet().remove(key, postId);
        log.debug("Bookmark remove: userId={}, postId={}, key={}", userId, postId, key); // 로그 추가
    }
    public String makeKey(Long userId) {
        return BOOKMARK_KEY_PREFIX + userId;
    }
    // 북마크 Sorted Set에 존재하지 않는 포스트입니다
    public boolean isMemberOfZSet(String key, Object value) {
        Double score = redisTemplate.opsForZSet().score(key, value);
        return score != null;
    }
    public BookmarkResponse toggleBookmark(Long userId, Long postId) {
        postRepository.findById(postId).orElseThrow(PostNotFound::new);

        userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        boolean isBookmarked = isMemberOfZSet(makeKey(userId), postId);
        boolean newStatus;

        if (isBookmarked) {
            removeBookmark(userId, postId);
            newStatus = false;
        }
        else {
            addBookmark(userId, postId);
            newStatus = true;
        }
        return new BookmarkResponse(postId, newStatus);
    }
}
