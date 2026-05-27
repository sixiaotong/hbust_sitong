package com.smartnote.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserProfileVO {
    private Long userId;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private Integer gender;
    private LocalDate birthday;
    private Integer followerCount;
    private Integer followingCount;
    private Integer noteCount;
    private Boolean isFollowing;
    private LocalDateTime createdAt;
}
