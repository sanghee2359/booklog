package com.api.booklog.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class UserEdit {
    private String name;
    private String email;
    private String password;

    @Builder
    public UserEdit(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}