package com.api.booklog.security;

import lombok.Getter;

@Getter
public class RefreshToken {
    public final String refreshToken;
    public RefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
