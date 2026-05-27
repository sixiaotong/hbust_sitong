package com.smartnote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartnote.dto.PageResult;
import com.smartnote.dto.UserProfileVO;
import com.smartnote.entity.User;
import com.smartnote.entity.UserFollow;
import com.smartnote.mapper.UserFollowMapper;
import com.smartnote.mapper.UserMapper;
import com.smartnote.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Override
    public UserProfileVO getUserProfile(Long userId, Long currentUserId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(user, vo);
        vo.setUserId(user.getId());

        if (currentUserId != null && !currentUserId.equals(userId)) {
            UserFollow existFollow = userFollowMapper.selectOne(
                    new LambdaQueryWrapper<UserFollow>()
                            .eq(UserFollow::getFollowerId, currentUserId)
                            .eq(UserFollow::getFollowingId, userId));
            vo.setIsFollowing(existFollow != null);
        }

        return vo;
    }

    @Override
    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }
        UserFollow exist = userFollowMapper.selectOne(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowerId, followerId)
                        .eq(UserFollow::getFollowingId, followingId));
        if (exist != null) {
            throw new RuntimeException("已关注该用户");
        }
        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        userFollowMapper.insert(follow);

        User follower = userMapper.selectById(followerId);
        if (follower != null) {
            follower.setFollowingCount((follower.getFollowingCount() == null ? 0 : follower.getFollowingCount()) + 1);
            userMapper.updateById(follower);
        }
        User following = userMapper.selectById(followingId);
        if (following != null) {
            following.setFollowerCount((following.getFollowerCount() == null ? 0 : following.getFollowerCount()) + 1);
            userMapper.updateById(following);
        }
    }

    @Override
    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        UserFollow exist = userFollowMapper.selectOne(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowerId, followerId)
                        .eq(UserFollow::getFollowingId, followingId));
        if (exist == null) {
            throw new RuntimeException("未关注该用户");
        }
        userFollowMapper.deleteById(exist.getId());

        User follower = userMapper.selectById(followerId);
        if (follower != null && follower.getFollowingCount() != null && follower.getFollowingCount() > 0) {
            follower.setFollowingCount(follower.getFollowingCount() - 1);
            userMapper.updateById(follower);
        }
        User following = userMapper.selectById(followingId);
        if (following != null && following.getFollowerCount() != null && following.getFollowerCount() > 0) {
            following.setFollowerCount(following.getFollowerCount() - 1);
            userMapper.updateById(following);
        }
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        UserFollow exist = userFollowMapper.selectOne(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowerId, followerId)
                        .eq(UserFollow::getFollowingId, followingId));
        return exist != null;
    }

    @Override
    public void updateAvatar(Long userId, String avatar) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setAvatar(avatar);
            userMapper.updateById(user);
        }
    }

    @Override
    public void updateBio(Long userId, String bio) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setBio(bio);
            userMapper.updateById(user);
        }
    }

    @Override
    public PageResult<UserProfileVO> listFollowers(Long userId, Long currentUserId, Integer page, Integer size) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowingId, userId).orderByDesc(UserFollow::getCreatedAt);
        Page<UserFollow> followPage = userFollowMapper.selectPage(new Page<>(page, size), wrapper);

        List<UserProfileVO> voList = new ArrayList<>();
        for (UserFollow follow : followPage.getRecords()) {
            User follower = userMapper.selectById(follow.getFollowerId());
            if (follower != null) {
                UserProfileVO vo = new UserProfileVO();
                BeanUtils.copyProperties(follower, vo);
                vo.setUserId(follower.getId());
                if (currentUserId != null && !currentUserId.equals(follower.getId())) {
                    vo.setIsFollowing(isFollowing(currentUserId, follower.getId()));
                }
                voList.add(vo);
            }
        }
        return new PageResult<>(voList, followPage.getTotal(), followPage.getSize(), followPage.getCurrent(), followPage.getPages());
    }

    @Override
    public PageResult<UserProfileVO> listFollowing(Long userId, Long currentUserId, Integer page, Integer size) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId).orderByDesc(UserFollow::getCreatedAt);
        Page<UserFollow> followPage = userFollowMapper.selectPage(new Page<>(page, size), wrapper);

        List<UserProfileVO> voList = new ArrayList<>();
        for (UserFollow follow : followPage.getRecords()) {
            User following = userMapper.selectById(follow.getFollowingId());
            if (following != null) {
                UserProfileVO vo = new UserProfileVO();
                BeanUtils.copyProperties(following, vo);
                vo.setUserId(following.getId());
                if (currentUserId != null && !currentUserId.equals(following.getId())) {
                    vo.setIsFollowing(isFollowing(currentUserId, following.getId()));
                }
                voList.add(vo);
            }
        }
        return new PageResult<>(voList, followPage.getTotal(), followPage.getSize(), followPage.getCurrent(), followPage.getPages());
    }
}
