package com.api.booklog.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PostEdit {
    @NotBlank(message = "제목을 입력해주세요.")
    private  String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private  String content;
    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
