package com.api.booklog.crypto;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Profile("test") // profile = test일 때만 bean으로 만든다
@Component
public class PlainPasswordEncoder implements PasswordEncoder{
    @Override
    public String encrypt(String rawPassword) {
        return rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encryptPassword) {
        return rawPassword.equals(encryptPassword);
    }
}
