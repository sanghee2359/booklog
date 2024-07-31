package com.api.booklog.config;

import com.api.booklog.domain.Users;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserPrincipal extends User { // security 의 User
    // role : 역할 -> 관리자, 사용자 등
    // authority : 권한 -> 글쓰기, 글 읽기, 사용자 정지시키기 등
    private final Long userId;
    public UserPrincipal(Users user) { // 도메인 user
        super(user.getEmail(), user.getPassword(),
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("WRITE")
                )); // 권한
        this.userId = user.getId();
    }

}
