package com.api.booklog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Data
@ConfigurationProperties(prefix = "book-log")
public class AppConfig {
    private byte[] jwtKey;

    public void setJwtKey(String jwtKey) {
        this.jwtKey = Base64.getDecoder().decode(jwtKey);
    }

    public byte[] getJwtKey() {
        return jwtKey;
    }
}