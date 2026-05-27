package com.smartnote.service;

import com.smartnote.dto.NoteRequest;
import com.smartnote.dto.NoteVO;
import com.smartnote.dto.PageResult;

public interface NoteService {
    PageResult<NoteVO> listNotes(Integer page, Integer size, String keyword, String category, Long currentUserId);
    NoteVO getNoteById(Long id, Long currentUserId);
    NoteVO createNote(Long userId, NoteRequest request);
    NoteVO updateNote(Long id, Long userId, NoteRequest request);
    void deleteNote(Long id, Long userId);
    boolean toggleLike(Long userId, Long noteId);
    boolean toggleFavorite(Long userId, Long noteId);
    PageResult<NoteVO> getUserNotes(Long userId, Integer page, Integer size);
    PageResult<NoteVO> getUserFavorites(Long userId, Integer page, Integer size);
}
