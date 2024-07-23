package com.api.booklog.crypto;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
@Profile("default")
@Component
public class ScryptPasswordEncoder implements PasswordEncoder{
    private static final SCryptPasswordEncoder encoder =
            new SCryptPasswordEncoder(
                    16,
                    8,
                    1,
                    32,
                    64);
    @Override
    public String encrypt(String password) {
        return encoder.encode(password);
    }
    @Override
    public boolean matches(String rawPassword, String encryptPassword) {
        return encoder.matches(rawPassword, encryptPassword);
    }

}
