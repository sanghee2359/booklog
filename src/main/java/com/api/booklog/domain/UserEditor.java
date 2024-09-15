package com.api.booklog.domain;

import lombok.Getter;

@Getter
public class UserEditor {

    private final String name;
    private final String email;
    private final String password;

    public UserEditor(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static UserEditorBuilder builder() {
        return new UserEditorBuilder();
    }

    public static class UserEditorBuilder {
        private String name;
        private String email;
        private String password;

        UserEditorBuilder() {
        }

        // name 필드를 수정할 때 null이 아니면 업데이트
        public UserEditorBuilder name(final String name) {
            if (name != null) {
                this.name = name;
            }
            return this;
        }

        // email 필드를 수정할 때 null이 아니면 업데이트
        public UserEditorBuilder email(final String email) {
            if (email != null) {
                this.email = email;
            }
            return this;
        }

        // password 필드를 수정할 때 null이 아니면 업데이트
        public UserEditorBuilder password(final String password) {
            if (password != null) {
                this.password = password;
            }
            return this;
        }

        // UserEditor 객체 생성
        public UserEditor build() {
            return new UserEditor(this.name, this.email, this.password);
        }

        @Override
        public String toString() {
            return "UserEditor.UserEditorBuilder(name=" + this.name + ", email=" + this.email + ", password=" + this.password + ")";
        }
    }
}