package com.api.booklog.controller;

import com.api.booklog.exception.AlreadyExistUserInformation;
import com.api.booklog.exception.RootException;
import com.api.booklog.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ExceptionController {


    @ResponseBody
    @ExceptionHandler(RootException.class)
    public ResponseEntity<ErrorResponse> rootException(RootException e) {
        int statusCode = e.getStatusCode();
        ErrorResponse responseBody = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .validation(e.getValidation()) // response json : validation 필드에 메세지 추가
                .build();


        ResponseEntity<ErrorResponse> response = ResponseEntity.status(statusCode).body(responseBody);
        return response;
    }

    @ExceptionHandler(AlreadyExistUserInformation.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistUserInformation(AlreadyExistUserInformation ex) {
        ErrorResponse responseBody = ErrorResponse.builder()
                .code("400")
                .message(ex.getMessage())
                .build();
        ResponseEntity<ErrorResponse> response = ResponseEntity.status(400).body(responseBody);

        return response;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> allException(Exception e) {
        ErrorResponse responseBody = ErrorResponse.builder()
                .code("500")
                .message(e.getMessage())
                .build();


        ResponseEntity<ErrorResponse> response = ResponseEntity.status(500).body(responseBody);
        return response;
    }


}
