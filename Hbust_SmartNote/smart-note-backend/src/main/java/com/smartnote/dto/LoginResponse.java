package com.smartnote.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String avatar;

    public LoginResponse(String token, Long userId, String username, String avatar) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
    }
}
