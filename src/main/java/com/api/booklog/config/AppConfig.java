package com.api.booklog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.Map;

import static com.api.booklog.security.Constants.ENCODER_ID;

@Configuration
public class AppConfig {
   @Bean
    public PasswordEncoder passwordEncoder() { // 어떤 디코딩/인코딩 방식을 사용할 지를 프레임워크에 알려준다
       Map<String, PasswordEncoder> encoders =
               Map.of(
                       ENCODER_ID,
                       new BCryptPasswordEncoder(),
                       "pbkdf2",
                       Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8(),
                       "scrypt",
                       SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
    return new DelegatingPasswordEncoder(ENCODER_ID, encoders);
   }

   @Bean
    public ObjectMapper objectMapper() {
       ObjectMapper mapper = new ObjectMapper();
       mapper.registerModule(new JavaTimeModule());
       mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
       return mapper;
   }
}