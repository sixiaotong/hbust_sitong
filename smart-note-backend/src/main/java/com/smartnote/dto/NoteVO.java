package com.smartnote.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteVO {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String title;
    private String content;
    private String coverImage;
    private String category;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Boolean isLiked;
    private Boolean isFavorited;
    private Boolean isFollowing;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String timeText;
}
