package com.api.booklog.config;

import com.api.booklog.domain.Users;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserPrincipal extends User { // security 의 User
    private final Long userId;
    public UserPrincipal(Users user) { // 도메인 user
        super(user.getEmail(), user.getPassword()
                , List.of(new SimpleGrantedAuthority("ADMIN"))); // 권한
        this.userId = user.getId();
    }

}
