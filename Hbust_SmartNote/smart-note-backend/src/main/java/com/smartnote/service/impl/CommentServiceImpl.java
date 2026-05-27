package com.smartnote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartnote.dto.CommentVO;
import com.smartnote.entity.Comment;
import com.smartnote.entity.Note;
import com.smartnote.entity.User;
import com.smartnote.mapper.CommentMapper;
import com.smartnote.mapper.NoteMapper;
import com.smartnote.mapper.UserMapper;
import com.smartnote.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public List<CommentVO> getCommentsByNoteId(Long noteId) {
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getNoteId, noteId)
                        .orderByDesc(Comment::getCreatedAt));

        return comments.stream().map(c -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(c, vo);
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getAvatar());
            }
            vo.setTimeText(formatTime(c.getCreatedAt()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public CommentVO addComment(Long userId, Long noteId, String content) {
        Comment comment = new Comment();
        comment.setNoteId(noteId);
        comment.setUserId(userId);
        comment.setContent(content);
        commentMapper.insert(comment);

        // 更新笔记评论数
        Note note = noteMapper.selectById(noteId);
        if (note != null) {
            note.setCommentCount((note.getCommentCount() == null ? 0 : note.getCommentCount()) + 1);
            noteMapper.updateById(note);
        }

        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        User user = userMapper.selectById(userId);
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
        }
        vo.setTimeText("刚刚");
        return vo;
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        long hours = ChronoUnit.HOURS.between(time, LocalDateTime.now());
        if (hours < 1) return "刚刚";
        if (hours < 24) return hours + "小时前";
        long days = ChronoUnit.DAYS.between(time, LocalDateTime.now());
        if (days < 7) return days + "天前";
        return time.toLocalDate().toString();
    }
}
