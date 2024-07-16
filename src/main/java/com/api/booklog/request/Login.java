package com.api.booklog.request;

import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.exception.InvalidRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Setter
@Getter
@ToString
public class Login {
    @NotBlank(message = "이메일을 입력해주세요.")
    private final String email;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String password;

    @Builder
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public void validate() {
        if(email == null) {
            throw new InvalidRequest("email", "이메일을 입력해주세요.");
        }
        if(password == null) {
            throw new InvalidRequest("password", "비밀번호를 입력해주세요.");
        }
    }
}
