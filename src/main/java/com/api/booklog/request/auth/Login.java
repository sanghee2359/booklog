package com.api.booklog.request.auth;

import com.api.booklog.exception.InvalidLoginInformation;
import com.api.booklog.exception.InvalidRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Login {
    private String email;
    private String password;

    @Builder
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public void validate() {
        if(email == null) {
            throw new InvalidLoginInformation("email", "이메일을 입력해주세요.");
        }
        if(password == null) {
            throw new InvalidLoginInformation("password", "비밀번호를 입력해주세요.");
        }
    }
}
