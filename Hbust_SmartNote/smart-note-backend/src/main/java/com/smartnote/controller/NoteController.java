package com.smartnote.controller;

import com.smartnote.common.Result;
import com.smartnote.dto.NoteRequest;
import com.smartnote.dto.NoteVO;
import com.smartnote.dto.PageResult;
import com.smartnote.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public Result<PageResult<NoteVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            PageResult<NoteVO> result = noteService.listNotes(page, size, keyword, category, userId);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<NoteVO> detail(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            NoteVO vo = noteService.getNoteById(id, userId);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<NoteVO> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody NoteRequest request) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            NoteVO vo = noteService.createNote(userId, request);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<NoteVO> update(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody NoteRequest request) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            NoteVO vo = noteService.updateNote(id, userId, request);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            noteService.deleteNote(id, userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> toggleLike(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            boolean liked = noteService.toggleLike(userId, id);
            Map<String, Object> data = new HashMap<>();
            data.put("liked", liked);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/favorite")
    public Result<Map<String, Object>> toggleFavorite(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            boolean favorited = noteService.toggleFavorite(userId, id);
            Map<String, Object> data = new HashMap<>();
            data.put("favorited", favorited);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
