package com.api.booklog.crypto;

import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Component;


public interface PasswordEncoder {
    String encrypt(String password);
    boolean matches(String rawPassword, String encryptPassword);

}
