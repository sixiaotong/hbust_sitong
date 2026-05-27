package com.smartnote.service;

import com.smartnote.dto.PageResult;
import com.smartnote.dto.UserProfileVO;

public interface UserService {
    UserProfileVO getUserProfile(Long userId, Long currentUserId);
    void follow(Long followerId, Long followingId);
    void unfollow(Long followerId, Long followingId);
    boolean isFollowing(Long followerId, Long followingId);
    void updateAvatar(Long userId, String avatar);
    void updateBio(Long userId, String bio);
    PageResult<UserProfileVO> listFollowers(Long userId, Long currentUserId, Integer page, Integer size);
    PageResult<UserProfileVO> listFollowing(Long userId, Long currentUserId, Integer page, Integer size);
}
