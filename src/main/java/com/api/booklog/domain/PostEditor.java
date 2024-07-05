package com.api.booklog.domain;

import lombok.Getter;

@Getter
public class PostEditor {

    private final String title;

    private final String content;

    public PostEditor(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public static PostEditorBuilder builder() {
        return new PostEditorBuilder();
    }

    public static class PostEditorBuilder {
        private String title;
        private String content;

        PostEditorBuilder() {
        }
        // builder 어노테이션 제거 & builder 클래스 복사
        public PostEditorBuilder title(final String title) {
            if(title != null) {
                this.title = title;
            }
            return this;
        }
        // 수정하지 않아 null값이 들어왔을 때, DB의 현재값이 저장되도록 수정
        public PostEditorBuilder content(final String content) {
            if(content != null) {
                this.content = content;
            }
            return this;
        }

        public PostEditor build() {
            return new PostEditor(this.title, this.content);
        }

        public String toString() {
            return "PostEditor.PostEditorBuilder(title=" + this.title + ", content=" + this.content + ")";
        }
    }
}
