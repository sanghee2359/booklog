package com.api.booklog.request.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SignInReq {
    private String email;
    private String password;
    @Builder
    public SignInReq(String name, String email, String password) {
        this.email = email;
        this.password = password;
    }
}
