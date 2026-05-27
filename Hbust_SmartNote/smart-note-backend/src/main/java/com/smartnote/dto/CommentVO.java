package com.smartnote.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long id;
    private Long noteId;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private LocalDateTime createdAt;
    private String timeText;
}
