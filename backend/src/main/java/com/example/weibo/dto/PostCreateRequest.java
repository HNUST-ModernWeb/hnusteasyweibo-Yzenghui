package com.example.weibo.dto;

public class PostCreateRequest {
    private String content;

    public PostCreateRequest() {
    }

    public PostCreateRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}