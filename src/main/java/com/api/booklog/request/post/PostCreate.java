package com.api.booklog.request.post;

import com.api.booklog.exception.InvalidRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PostCreate {
    private String title;

    private String content;
    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public void validate() {
        if(title != null && title.contains("바보")) {
            throw new InvalidRequest("title", "제목에 바보를 포함할 수 없습니다.");
        }
    }
}
