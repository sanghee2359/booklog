package com.api.booklog.controller;

import com.api.booklog.request.PostCreate;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.ReflectionUtils.getField;


@Slf4j
@RestController
public class PostController {
    @GetMapping("/gets")
    public String get() {
        return "HEllo world";
    }
    @PostMapping("/posts")
    public Map<String, String> post(@RequestBody @Valid PostCreate params, BindingResult result) throws Exception {
        log.info("params={}", params);
        if(result.hasErrors()){
            List<FieldError> fieldError = result.getFieldErrors();
            FieldError firstFieldError = fieldError.get(0);

            String fieldName = firstFieldError.getField();
            String errorMessage = firstFieldError.getDefaultMessage();

            Map<String, String> error = new HashMap<>();
            error.put(fieldName, errorMessage);
            return error;
        }
        return Map.of();
    }
}
