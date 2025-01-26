package com.api.booklog.response;

import com.api.booklog.security.exception.ErrorCode;
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
//@JsonInclude(value = JsonInclude.Include.NON_EMPTY) -> 비어있지 않은 데이터만 출력
public class ErrorResponse {
    private final String code;
    private final String message;
    private final Map<String, String> validation;

    @Builder
    public ErrorResponse(String code, String message, Map<String, String> validation) {
        this.code = code;
        this.message = message;
        this.validation = validation != null ? validation : new HashMap<>();
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getHttpStatus().toString();
        this.message = errorCode.getMessage();
        this.validation = null;
    }
}
