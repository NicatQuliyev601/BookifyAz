package com.bookifyaz.bookifyaz.dto.response;

public class ErrorResponseDto {
    int status;
    String title;
    String details;

    public ErrorResponseDto(Builder builder) {
        this.status = builder.status;
        this.title = builder.title;
        this.details = builder.details;
    }

    public static class Builder {
        private int status;
        private String title;
        private String details;

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public ErrorResponseDto build() {
            return new ErrorResponseDto(this);
        }
    }
}
