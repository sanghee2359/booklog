package com.api.booklog.security;

import com.api.booklog.domain.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;

@Getter
@RequiredArgsConstructor
public class RefreshToken {
    public String refreshToken;
    public User user;
}
