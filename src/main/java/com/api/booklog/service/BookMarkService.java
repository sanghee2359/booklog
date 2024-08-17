package com.api.booklog.service;

import com.api.booklog.response.BookMarkResponse;
import com.api.booklog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostService postService;
    private static final String BOOKMARK_KEY_PREFIX = "user:bookmarks:";

    public void addBookmark(Long userId, Long postId) {
        String key = BOOKMARK_KEY_PREFIX + userId;
        redisTemplate.opsForSet().add(key, postId);
    }

    public BookMarkResponse getBookmarks(Long userId) {
        String key = BOOKMARK_KEY_PREFIX + userId;
        // postResponse list
        List<Long> bookmarkIds = Objects.requireNonNull(redisTemplate.opsForSet().members(key)).stream()
                .map(item -> (Long) item)
                .toList();

        List<PostResponse> bookmarks = bookmarkIds.stream()
                .map(postService::get)
                .toList();
        return BookMarkResponse.builder()
                .userId(userId)
                .bookmarks(bookmarks)
                .build();
    }

    public void removeBookmark(Long userId, Long postId) {
        String key = BOOKMARK_KEY_PREFIX + userId;
        redisTemplate.opsForSet().remove(key, postId);
    }
}
