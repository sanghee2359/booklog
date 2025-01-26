package com.api.booklog.request.comment;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class CommentCreate {
    private String author;
    private String password;
    @Length(min = 10, max = 1000, message = "댓글을 10~1000자 입력해주세요.")
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @Builder
    public CommentCreate(String author, String password, String content) {
        this.author = author;
        this.password = password;
        this.content = content;
    }
}
