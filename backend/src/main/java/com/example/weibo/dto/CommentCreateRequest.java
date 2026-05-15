package com.example.weibo.dto;

public class CommentCreateRequest {
    private Integer postId;
    private String content;

    public CommentCreateRequest() {
    }

    public CommentCreateRequest(Integer postId, String content) {
        this.postId = postId;
        this.content = content;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}