package com.api.booklog.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SignUpReq {
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @Builder
    public SignUpReq(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
