package com.api.booklog.response;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * {
 *     "code" : "400",
 *     "message" : "잘못된 요청입니다.",
 *     "validation" : {
 *         "title" : "제목을 입력해주세요",
 *         "content" : "내용을 입력해주세요"
 *     }
 *  }
 */
@Getter
public class ErrorResponse {
    private final String code;
    private final String message;
    private Map<String, String> validation = new HashMap<>();
    public void addValidation(String fieldName, String errorMessage) {

        validation.put(fieldName, errorMessage);
    }
    @Builder
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
