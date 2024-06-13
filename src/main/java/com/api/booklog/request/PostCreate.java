package com.api.booklog.request;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class PostCreate {
    private String title;
    private String content;

}
