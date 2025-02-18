package com.api.booklog.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SignInReq {
    @NotNull
    private String email;
    @NotNull
    private String password;
    @Builder
    public SignInReq(String name, String email, String password) {
        this.email = email;
        this.password = password;
    }
}
