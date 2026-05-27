package com.smartnote.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NoteRequest {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
    private String coverImage;
    private String category;
}
