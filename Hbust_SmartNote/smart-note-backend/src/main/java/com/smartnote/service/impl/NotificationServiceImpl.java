package com.smartnote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartnote.dto.NotificationVO;
import com.smartnote.entity.*;
import com.smartnote.mapper.*;
import com.smartnote.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

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
    public List<NotificationVO> getNotifications(Long userId) {
        List<NotificationVO> list = new ArrayList<>();

        // 当前用户的所有笔记ID
        List<Note> myNotes = noteMapper.selectList(
                new LambdaQueryWrapper<Note>().eq(Note::getUserId, userId));
        List<Long> myNoteIds = myNotes.stream().map(Note::getId).collect(Collectors.toList());
        Map<Long, Note> noteMap = myNotes.stream().collect(Collectors.toMap(Note::getId, n -> n));

        if (!myNoteIds.isEmpty()) {
            // 别人对我笔记的点赞
            List<UserLike> likes = userLikeMapper.selectList(
                    new LambdaQueryWrapper<UserLike>()
                            .in(UserLike::getNoteId, myNoteIds)
                            .ne(UserLike::getUserId, userId));
            for (UserLike like : likes) {
                User u = userMapper.selectById(like.getUserId());
                Note note = noteMap.get(like.getNoteId());
                if (u != null && note != null) {
                    list.add(new NotificationVO("like", u.getId(), u.getUsername(), u.getAvatar(),
                            note.getId(), note.getTitle(), note.getCoverImage(), null,
                            formatTime(like.getCreatedAt()), like.getCreatedAt()));
                }
            }

            // 别人对我笔记的评论
            List<Comment> comments = commentMapper.selectList(
                    new LambdaQueryWrapper<Comment>()
                            .in(Comment::getNoteId, myNoteIds)
                            .ne(Comment::getUserId, userId));
            for (Comment c : comments) {
                User u = userMapper.selectById(c.getUserId());
                Note note = noteMap.get(c.getNoteId());
                if (u != null && note != null) {
                    list.add(new NotificationVO("comment", u.getId(), u.getUsername(), u.getAvatar(),
                            note.getId(), note.getTitle(), note.getCoverImage(), c.getContent(),
                            formatTime(c.getCreatedAt()), c.getCreatedAt()));
                }
            }

            // 别人对我笔记的收藏
            List<UserFavorite> favs = userFavoriteMapper.selectList(
                    new LambdaQueryWrapper<UserFavorite>()
                            .in(UserFavorite::getNoteId, myNoteIds)
                            .ne(UserFavorite::getUserId, userId));
            for (UserFavorite fav : favs) {
                User u = userMapper.selectById(fav.getUserId());
                Note note = noteMap.get(fav.getNoteId());
                if (u != null && note != null) {
                    list.add(new NotificationVO("favorite", u.getId(), u.getUsername(), u.getAvatar(),
                            note.getId(), note.getTitle(), note.getCoverImage(), null,
                            formatTime(fav.getCreatedAt()), fav.getCreatedAt()));
                }
            }
        }

        // 别人关注了我
        List<UserFollow> follows = userFollowMapper.selectList(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowingId, userId));
        for (UserFollow follow : follows) {
            User u = userMapper.selectById(follow.getFollowerId());
            if (u != null) {
                list.add(new NotificationVO("follow", u.getId(), u.getUsername(), u.getAvatar(),
                        null, null, null, null,
                        formatTime(follow.getCreatedAt()), follow.getCreatedAt()));
            }
        }

        // 按时间倒序
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return list;
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
