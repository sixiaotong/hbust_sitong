package com.smartnote.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVO {
    private String type;       // like, comment, follow, favorite
    private Long fromUserId;
    private String fromUsername;
    private String fromAvatar;
    private Long noteId;
    private String noteTitle;
    private String noteCover;
    private String content;    // comment text
    private String timeText;
    private LocalDateTime createdAt;
}
