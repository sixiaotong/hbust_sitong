package com.smartnote.controller;

import com.smartnote.common.Result;
import com.smartnote.dto.CommentVO;
import com.smartnote.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/notes/{noteId}/comments")
    public Result<List<CommentVO>> list(@PathVariable Long noteId) {
        try {
            List<CommentVO> list = commentService.getCommentsByNoteId(noteId);
            return Result.success(list);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/notes/{noteId}/comments")
    public Result<CommentVO> add(
            @PathVariable Long noteId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody Map<String, String> body) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            CommentVO vo = commentService.addComment(userId, noteId, body.get("content"));
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
