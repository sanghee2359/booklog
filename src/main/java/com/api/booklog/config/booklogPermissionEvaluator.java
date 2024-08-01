package com.api.booklog.config;

import com.api.booklog.exception.PostNotFound;
import com.api.booklog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
@Slf4j
@RequiredArgsConstructor
public class booklogPermissionEvaluator implements PermissionEvaluator {
    private final PostRepository postRepository;
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        var userPrincipal = (UserPrincipal)authentication.getPrincipal();

        var post = postRepository.findById((Long) targetId) // findById -> null?
                .orElseThrow(PostNotFound::new);

        // post의 userId와 현재로그인된 userId가 같은지 확인
        if(!post.getUserId().equals(userPrincipal.getUserId())){
            log.error("[인가 실패] 해당 사용자가 작성한 글이 아닙니다.");
            return false;
        }
        return true;
    }
}
