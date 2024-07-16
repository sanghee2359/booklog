package com.api.booklog.exception;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Getter
public abstract class RootException extends RuntimeException{

    public final Map<String, String> validation = new HashMap<>();
    public RootException(String message) {
        super(message);
    }

    public RootException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();
    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }

}
