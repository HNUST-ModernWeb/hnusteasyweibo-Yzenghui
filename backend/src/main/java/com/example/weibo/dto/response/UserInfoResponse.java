package com.example.weibo.dto.response;

import java.time.LocalDateTime;

public class UserInfoResponse {
    private Integer id;
    private String username;
    private String avatar;
    private LocalDateTime createTime;

    public UserInfoResponse() {
    }

    public UserInfoResponse(Integer id, String username, String avatar, LocalDateTime createTime) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}