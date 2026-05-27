package com.smartnote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartnote.dto.NoteRequest;
import com.smartnote.dto.NoteVO;
import com.smartnote.dto.PageResult;
import com.smartnote.entity.*;
import com.smartnote.mapper.*;
import com.smartnote.service.NoteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserLikeMapper userLikeMapper;

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Override
    public PageResult<NoteVO> listNotes(Integer page, Integer size, String keyword, String category, Long currentUserId) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Note::getCreatedAt);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Note::getTitle, keyword).or().like(Note::getContent, keyword));
        }
        if (category != null && !category.isEmpty() && !"全部".equals(category)) {
            wrapper.eq(Note::getCategory, category);
        }

        Page<Note> notePage = noteMapper.selectPage(new Page<>(page, size), wrapper);
        List<NoteVO> voList = new ArrayList<>();
        for (Note note : notePage.getRecords()) {
            NoteVO vo = toVO(note, currentUserId);
            vo.setContent(null);
            voList.add(vo);
        }
        return new PageResult<>(voList, notePage.getTotal(), notePage.getSize(), notePage.getCurrent(), notePage.getPages());
    }

    @Override
    public NoteVO getNoteById(Long id, Long currentUserId) {
        Note note = noteMapper.selectById(id);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }
        note.setViewCount(note.getViewCount() + 1);
        noteMapper.updateById(note);
        return toVO(note, currentUserId);
    }

    @Override
    @Transactional
    public NoteVO createNote(Long userId, NoteRequest request) {
        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCoverImage(request.getCoverImage());
        note.setCategory(request.getCategory() != null ? request.getCategory() : "全部");
        note.setViewCount(0);
        note.setLikeCount(0);
        note.setCommentCount(0);
        note.setFavoriteCount(0);
        noteMapper.insert(note);

        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setNoteCount((user.getNoteCount() == null ? 0 : user.getNoteCount()) + 1);
            userMapper.updateById(user);
        }

        return toVO(note, userId);
    }

    @Override
    public NoteVO updateNote(Long id, Long userId, NoteRequest request) {
        Note note = noteMapper.selectById(id);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改他人笔记");
        }
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCoverImage(request.getCoverImage());
        if (request.getCategory() != null) {
            note.setCategory(request.getCategory());
        }
        noteMapper.updateById(note);
        return toVO(note, userId);
    }

    @Override
    @Transactional
    public void deleteNote(Long id, Long userId) {
        Note note = noteMapper.selectById(id);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除他人笔记");
        }
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getNoteId, id));
        userLikeMapper.delete(new LambdaQueryWrapper<UserLike>().eq(UserLike::getNoteId, id));
        userFavoriteMapper.delete(new LambdaQueryWrapper<UserFavorite>().eq(UserFavorite::getNoteId, id));
        noteMapper.deleteById(id);

        User user = userMapper.selectById(userId);
        if (user != null && user.getNoteCount() != null && user.getNoteCount() > 0) {
            user.setNoteCount(user.getNoteCount() - 1);
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) return false;

        UserLike existLike = userLikeMapper.selectOne(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId)
                        .eq(UserLike::getNoteId, noteId));

        if (existLike != null) {
            userLikeMapper.deleteById(existLike.getId());
            note.setLikeCount(Math.max(0, note.getLikeCount() - 1));
            noteMapper.updateById(note);
            return false;
        } else {
            UserLike like = new UserLike();
            like.setUserId(userId);
            like.setNoteId(noteId);
            userLikeMapper.insert(like);
            note.setLikeCount(note.getLikeCount() + 1);
            noteMapper.updateById(note);
            return true;
        }
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long userId, Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) return false;

        UserFavorite existFav = userFavoriteMapper.selectOne(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getNoteId, noteId));

        if (existFav != null) {
            userFavoriteMapper.deleteById(existFav.getId());
            note.setFavoriteCount(Math.max(0, note.getFavoriteCount() - 1));
            noteMapper.updateById(note);
            return false;
        } else {
            UserFavorite fav = new UserFavorite();
            fav.setUserId(userId);
            fav.setNoteId(noteId);
            userFavoriteMapper.insert(fav);
            note.setFavoriteCount(note.getFavoriteCount() + 1);
            noteMapper.updateById(note);
            return true;
        }
    }

    @Override
    public PageResult<NoteVO> getUserNotes(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, userId).orderByDesc(Note::getCreatedAt);
        Page<Note> notePage = noteMapper.selectPage(new Page<>(page, size), wrapper);
        List<NoteVO> voList = new ArrayList<>();
        for (Note note : notePage.getRecords()) {
            NoteVO vo = toVO(note, userId);
            vo.setContent(null);
            voList.add(vo);
        }
        return new PageResult<>(voList, notePage.getTotal(), notePage.getSize(), notePage.getCurrent(), notePage.getPages());
    }

    @Override
    public PageResult<NoteVO> getUserFavorites(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<UserFavorite> favWrapper = new LambdaQueryWrapper<>();
        favWrapper.eq(UserFavorite::getUserId, userId).orderByDesc(UserFavorite::getCreatedAt);
        Page<UserFavorite> favPage = userFavoriteMapper.selectPage(new Page<>(page, size), favWrapper);

        List<NoteVO> voList = new ArrayList<>();
        for (UserFavorite fav : favPage.getRecords()) {
            Note note = noteMapper.selectById(fav.getNoteId());
            if (note != null) {
                NoteVO vo = toVO(note, userId);
                vo.setContent(null);
                voList.add(vo);
            }
        }
        return new PageResult<>(voList, favPage.getTotal(), favPage.getSize(), favPage.getCurrent(), favPage.getPages());
    }

    private NoteVO toVO(Note note, Long currentUserId) {
        NoteVO vo = new NoteVO();
        BeanUtils.copyProperties(note, vo);
        User user = userMapper.selectById(note.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
        }
        vo.setTimeText(formatTime(note.getCreatedAt()));

        if (currentUserId != null) {
            UserLike existLike = userLikeMapper.selectOne(
                    new LambdaQueryWrapper<UserLike>()
                            .eq(UserLike::getUserId, currentUserId)
                            .eq(UserLike::getNoteId, note.getId()));
            vo.setIsLiked(existLike != null);

            UserFavorite existFav = userFavoriteMapper.selectOne(
                    new LambdaQueryWrapper<UserFavorite>()
                            .eq(UserFavorite::getUserId, currentUserId)
                            .eq(UserFavorite::getNoteId, note.getId()));
            vo.setIsFavorited(existFav != null);

            if (!currentUserId.equals(note.getUserId())) {
                UserFollow existFollow = userFollowMapper.selectOne(
                        new LambdaQueryWrapper<UserFollow>()
                                .eq(UserFollow::getFollowerId, currentUserId)
                                .eq(UserFollow::getFollowingId, note.getUserId()));
                vo.setIsFollowing(existFollow != null);
            }
        }

        return vo;
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        long hours = ChronoUnit.HOURS.between(time, LocalDateTime.now());
        if (hours < 1) {
            long minutes = ChronoUnit.MINUTES.between(time, LocalDateTime.now());
            return minutes <= 0 ? "刚刚" : minutes + "分钟前";
        }
        if (hours < 24) return hours + "小时前";
        long days = ChronoUnit.DAYS.between(time, LocalDateTime.now());
        if (days < 7) return days + "天前";
        return time.toLocalDate().toString();
    }
}
