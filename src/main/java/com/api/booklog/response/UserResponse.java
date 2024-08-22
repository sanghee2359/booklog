package com.api.booklog.response;

import com.api.booklog.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private final Long id;
    private final String name;
    public UserResponse(Users user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}
