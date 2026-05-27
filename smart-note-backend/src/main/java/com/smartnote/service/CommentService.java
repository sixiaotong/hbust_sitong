package com.smartnote.service;

import com.smartnote.dto.CommentVO;

import java.util.List;

public interface CommentService {
    List<CommentVO> getCommentsByNoteId(Long noteId);
    CommentVO addComment(Long userId, Long noteId, String content);
}
